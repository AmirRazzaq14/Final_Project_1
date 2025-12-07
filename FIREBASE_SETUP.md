# Firebase Authentication Setup Guide

This application uses **Firebase Authentication** for user login and registration, while all other data (profiles, workouts, progress) is stored locally.

## Setup Instructions

### 1. Create a Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or select an existing project
3. Follow the setup wizard

### 2. Enable Authentication

1. In Firebase Console, go to **Authentication** > **Sign-in method**
2. Enable **Email/Password** provider
3. Click "Save"

### 3. Generate Service Account Key

1. In Firebase Console, go to **Project Settings** (gear icon)
2. Go to the **Service accounts** tab
3. Click **Generate new private key**
4. Save the JSON file as `firebase-service-account-key.json`
5. Place this file in the **project root directory** (same level as `pom.xml`)

### 4. Get Firebase Web API Key (Optional - for password verification)

1. In Firebase Console, go to **Project Settings** > **General**
2. Under "Your apps", find or create a Web app
3. Copy the **API Key**
4. (Optional) You can add this to a config file for REST API password verification

## How It Works

### Authentication Flow

- **Registration**: Creates user in Firebase Auth (if service account key is present)
- **Login**: Verifies user exists in Firebase, then verifies password
- **Fallback**: If Firebase is not configured, uses local file storage for authentication

### Data Storage

- **Authentication**: Firebase Auth (if configured) or local storage (fallback)
- **User Profiles**: Local storage (`user_profiles.dat`)
- **Workout Plans**: Local storage (`workout_plans.dat`)
- **Progress Data**: Local storage (`progress_data.dat`)
- **Workout Sessions**: Local storage (`workout_sessions.dat`)

## File Structure

```
Final_Project_1/
├── pom.xml
├── firebase-service-account-key.json  ← Place your service account key here
├── src/
└── ...
```

## Troubleshooting

### Firebase Not Working

If you see "Firebase service account key not found" in console:
- Make sure `firebase-service-account-key.json` is in the project root
- Check that the file name is exactly `firebase-service-account-key.json`
- Verify the JSON file is valid

### Authentication Falls Back to Local Storage

If Firebase is not configured, the app will automatically use local file storage for authentication. This allows the app to work without Firebase setup, but users will only be stored locally.

### Password Verification

Currently, password verification uses a hybrid approach:
- Checks if user exists in Firebase
- Verifies password against local storage (backup)
- For production, implement Firebase Auth REST API for proper password verification

## Security Notes

⚠️ **Important**: 
- Never commit `firebase-service-account-key.json` to version control
- Add it to `.gitignore`
- The service account key has admin privileges - keep it secure

## Next Steps

For production deployment:
1. Implement Firebase Auth REST API for password verification
2. Add proper password hashing
3. Set up environment variables for API keys
4. Implement proper error handling and user feedback


