import pandas as pd
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score, roc_auc_score, roc_curve, precision_recall_curve
from sklearn.impute import SimpleImputer
import joblib

# Load the dataset
data = pd.read_csv('Testing.csv')

# EDA - Exploratory Data Analysis
print("Data Head:")
print(data.head())
print("\nData Info:")
print(data.info())
print("\nData Description:")
print(data.describe())

# Checking for null values
print("\nMissing Values (before imputation):")
print(data.isnull().sum())

# Impute missing values with the median for numerical columns
imputer = SimpleImputer(strategy='median')
data_imputed = pd.DataFrame(imputer.fit_transform(data), columns=data.columns)

# Checking for null values again to ensure imputation worked
print("\nMissing Values (after imputation):")
print(data_imputed.isnull().sum())

# Data Visualization
plt.figure(figsize=(10, 8))
sns.heatmap(data_imputed.corr(), annot=True, cmap='coolwarm')
plt.title('Correlation Heatmap')
plt.show()

# Pairplot
sns.pairplot(data_imputed, hue='Outcome')
plt.show()

# Histograms
data_imputed.hist(bins=15, figsize=(20, 15), grid=False, layout=(3, 3))
plt.show()

# Checking the distribution of the target variable
sns.countplot(x='Outcome', data=data_imputed)
plt.title('Distribution of Outcome')
plt.show()

# Splitting the data into features and target variable
X = data_imputed.drop('Outcome', axis=1)
y = data_imputed['Outcome']

# Scaling the data
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

# Train-test split
X_train, X_test, y_train, y_test = train_test_split(X_scaled, y, test_size=0.2, random_state=42)

# Model building
model = LogisticRegression()
model.fit(X_train, y_train)

# Save the trained model and scaler
joblib.dump(model, 'diabetes_model.pkl')
joblib.dump(scaler, 'scaler.pkl')

# Predictions
y_pred = model.predict(X_test)
y_pred_prob = model.predict_proba(X_test)[:, 1]

# Evaluation
print('Confusion Matrix:')
print(confusion_matrix(y_test, y_pred))

print('\nClassification Report:')
print(classification_report(y_test, y_pred))

print('\nAccuracy Score:')
print(accuracy_score(y_test, y_pred))

print('\nROC-AUC Score:')
roc_auc = roc_auc_score(y_test, y_pred_prob)
print(roc_auc)

# Visualize the confusion matrix
plt.figure(figsize=(6, 5))
sns.heatmap(confusion_matrix(y_test, y_pred), annot=True, fmt='d', cmap='coolwarm', cbar=False)
plt.xlabel('Predicted')
plt.ylabel('Actual')
plt.title('Confusion Matrix')
plt.show()

# ROC Curve
fpr, tpr, _ = roc_curve(y_test, y_pred_prob)
plt.figure()
plt.plot(fpr, tpr, color='darkorange', lw=2, label='ROC curve (area = %0.2f)' % roc_auc)
plt.plot([0, 1], [0, 1], color='navy', lw=2, linestyle='--')
plt.xlim([0.0, 1.0])
plt.ylim([0.0, 1.05])
plt.xlabel('False Positive Rate')
plt.ylabel('True Positive Rate')
plt.title('Receiver Operating Characteristic (ROC) Curve')
plt.legend(loc="lower right")
plt.show()

# Precision-Recall Curve
precision, recall, _ = precision_recall_curve(y_test, y_pred_prob)
plt.figure()
plt.plot(recall, precision, marker='.', color='b', label='Precision-Recall curve')
plt.xlabel('Recall')
plt.ylabel('Precision')
plt.title('Precision-Recall Curve')
plt.legend(loc="lower left")
plt.show()

