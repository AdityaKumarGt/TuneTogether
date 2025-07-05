// firebase.js
import admin from "firebase-admin";
import { readFileSync } from "fs";

// Replace with the path to your service account key
const serviceAccount = JSON.parse(
  readFileSync("config/firebaseAdmin.json", "utf8")
);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

export default admin;
