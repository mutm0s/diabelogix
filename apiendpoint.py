from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)

# Load the trained model and scaler
model = joblib.load('diabetes_model.pkl')
scaler = joblib.load('scaler.pkl')

@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    features = [
        data['Pregnancies'], data['Glucose'], data['BloodPressure'], 
        data['SkinThickness'], data['Insulin'], data['BMI'], 
        data['DiabetesPedigreeFunction'], data['Age']
    ]
    features = np.array(features).reshape(1, -1)
    
    # Scale the features
    features = scaler.transform(features)
    
    prediction = model.predict(features)
    prediction_prob = model.predict_proba(features)[:, 1]
    
    return jsonify({
        'prediction': int(prediction[0]),
        'probability': float(prediction_prob[0])
    })

if __name__ == '__main__':
    app.run(debug=True)
