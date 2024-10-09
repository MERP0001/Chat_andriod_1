const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNotificationOnMessage = functions.firestore
    .document("id_chat/{messageId}") // Escuchar en la colección id_chat
    .onCreate(async (snapshot, context) => {
        const messageData = snapshot.data();
        const receiverId = messageData.id_receiver; // ID del receptor del mensaje

        // Busca el token FCM del receptor en la colección 'users'
        const userRef = admin.firestore().collection("users").doc(receiverId);
        const userSnapshot = await userRef.get();

        if (!userSnapshot.exists) {
            console.log(`No se encontró el usuario con ID: ${receiverId}`);
            return;
        }

        const fcmToken = userSnapshot.data().token;

        if (!fcmToken) {
            console.log(`El usuario ${receiverId} no tiene un token FCM registrado.`);
            return;
        }

        admin.firestore().collection("users").doc(messageData.id_sender).get()
            .then((doc) => {
               if (doc.exists) {
                  senderName = doc.data().name; 
               } else {
                     console.log("No such document!");
               }
            })
            .catch((error) => {
               console.log("Error getting document:", error);
            });


        // Crear el payload de la notificación
        const payload = {
            notification: {
                title: `Nuevo mensaje de ${senderName}`,
                body: messageData.id_message,
            },
        };

        // Crear el mensaje multicast con el token del receptor
        const message = {
            tokens: [fcmToken], // Lista de tokens a los que se les enviará la notificación
            ...payload,
        };

        // Enviar la notificación utilizando sendEachForMulticast
        return admin.messaging().sendEachForMulticast(message)
            .then((response) => {
                console.log("Notificación enviada con éxito:", response);
            })
            .catch((error) => {
                console.log("Error enviando la notificación:", error);
            });
    });
