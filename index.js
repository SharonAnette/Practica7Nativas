const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendNotification = functions.https.onCall(async (data, context) => {
  const { token, title, body } = data;



  try {
    const message = {
      token,
      notification: { title, body },
    };

    await admin.messaging().send(message);

    await admin.firestore().collection("notifications").add({
      toToken: token,
      title,
      body,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
    });

    return {
      success: true,
      message: "Notificación enviada y guardada",
    };
  } catch (error) {
    console.error("Error al enviar notificación:", error);
    throw new functions.https.HttpsError(
      "internal",
      "Error al enviar notificación"
    );
  }
});
