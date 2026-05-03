import admin from 'firebase-admin';
import { readFileSync } from 'fs';

const serviceAccount = JSON.parse(readFileSync('./serviceAccountKey.json'));

if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });
}

export const verifyFirebaseToken = async (idToken) => {
  const decoded = await admin.auth().verifyIdToken(idToken);
  return decoded; // { uid, phone_number, ... }
};