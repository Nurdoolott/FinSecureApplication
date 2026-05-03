import admin from 'firebase-admin';

if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(
      JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT)
    )
  });
}

export const verifyFirebaseToken = async (idToken) => {
  const decoded = await admin.auth().verifyIdToken(idToken);
  return decoded;
};