from flask import Flask, Response, request, jsonify
import cv2
import os
import jwt
import datetime
from functools import wraps
from threading import Thread, Event

app = Flask(__name__)

# Secret key for JWT
app.config['SECRET_KEY'] = 'your_secret_key'

# Global variables for video broadcasting and recording
camera = cv2.VideoCapture(0)
is_broadcasting = Event()
is_recording = Event()
recording_thread = None
video_writer = None

def token_required(f):
    """Decorator to secure endpoints with JWT."""
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.headers.get('x-access-token')
        if not token:
            return jsonify({'message': 'Token is missing!'}), 401
        try:
            jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
        except jwt.ExpiredSignatureError:
            return jsonify({'message': 'Token has expired!'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'message': 'Token is invalid!'}), 401
        return f(*args, **kwargs)
    return decorated

@app.route('/login', methods=['POST'])
def login():
    """Endpoint to generate JWT token."""
    auth = request.json
    if not auth or not auth.get('username') or not auth.get('password'):
        return jsonify({'message': 'Missing username or password!'}), 400

    if auth['username'] == 'admin' and auth['password'] == 'password':
        token = jwt.encode({
            'user': auth['username'],
            'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=1)
        }, app.config['SECRET_KEY'], algorithm='HS256')
        return jsonify({'token': token})
    
    return jsonify({'message': 'Invalid credentials!'}), 401

def generate_frames():
    """Yield frames for video streaming."""
    while is_broadcasting.is_set():
        success, frame = camera.read()
        if not success:
            break
        _, buffer = cv2.imencode('.jpg', frame)
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + buffer.tobytes() + b'\r\n')

def record_video():
    """Record video to a file."""
    global video_writer
    if not os.path.exists("video"):
        os.makedirs("video")
    fourcc = cv2.VideoWriter_fourcc(*'XVID')
    video_writer = cv2.VideoWriter('video/recording.avi', fourcc, 20.0, (640, 480))
    
    while is_recording.is_set():
        success, frame = camera.read()
        if success:
            video_writer.write(frame)
    video_writer.release()

@app.route('/start_broadcast', methods=['POST'])
@token_required
def start_broadcast():
    """Start broadcasting video."""
    if not is_broadcasting.is_set():
        is_broadcasting.set()
        return "Broadcasting started", 200
    return "Broadcast already started", 400

@app.route('/stop_broadcast', methods=['POST'])
@token_required
def stop_broadcast():
    """Stop broadcasting video."""
    if is_broadcasting.is_set():
        is_broadcasting.clear()
        return "Broadcasting stopped", 200
    return "No active broadcast to stop", 400

@app.route('/start_recording', methods=['POST'])
@token_required
def start_recording():
    """Start recording video."""
    global recording_thread
    if not is_recording.is_set():
        is_recording.set()
        recording_thread = Thread(target=record_video)
        recording_thread.start()
        return "Recording started", 200
    return "Recording already in progress", 400

@app.route('/stop_recording', methods=['POST'])
@token_required
def stop_recording():
    """Stop recording video."""
    if is_recording.is_set():
        is_recording.clear()
        if recording_thread:
            recording_thread.join()
        return "Recording stopped", 200
    return "No recording to stop", 400

@app.route('/video_feed')
@token_required
def video_feed():
    """Provide a video feed."""
    if is_broadcasting.is_set():
        return Response(generate_frames(),
                        mimetype='multipart/x-mixed-replace; boundary=frame')
    else:
        return "Broadcast not active", 400




if __name__ == '__main__':
    try:
        app.run(host='0.0.0.0', port=5000, threaded=True)
    finally:
        camera.release()
        if video_writer:
            video_writer.release()
