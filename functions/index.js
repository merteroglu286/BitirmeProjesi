const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.incrementAllNoticeCredits = functions.pubsub.schedule('every 1 hours').onRun((context) => {
  const database = admin.database();
  return database.ref('/Users').once('value')
      .then((usersSnapshot) => {
          const promises = [];
          usersSnapshot.forEach((userSnapshot) => {
              const uid = userSnapshot.key;
              const userModel = userSnapshot.val();
              const noticeCredit = userModel.noticeCredit;
              if (noticeCredit < 3) {
                  const newNoticeCredit = noticeCredit + 1;
                  const userRef = database.ref(`/Users/${uid}`);
                  promises.push(userRef.update({ noticeCredit: newNoticeCredit }));
              }
          });
          return Promise.all(promises);
      })
      .catch((error) => {
          console.error(error);
          return null;
      });
});


exports.deleteOldPhotos = functions.region('us-central1').pubsub.schedule('every 1 minutes').onRun((context) => {
  const database = admin.database();
  const storage = admin.storage();

  return database.ref('/Users').once('value')
    .then((usersSnapshot) => {
      const promises = [];

      usersSnapshot.forEach((userSnapshot) => {
        const userId = userSnapshot.key;
        const photosRef = storage.ref(`${userId}/SharedPhotos`);

        // List all items in SharedPhotos folder
        promises.push(photosRef.listAll().then((listResult) => {
          const now = Date.now();

          // Filter out items older than 2 days
          const oldPhotos = listResult.items.filter((item) => {
            const ageInMs = now - item.updated;
            const ageInDays = ageInMs / (24 * 60 * 60 * 1000);
            return ageInDays > 2;
          });

          // Delete each old photo
          const deletePromises = oldPhotos.map((photoRef) => photoRef.delete());
          return Promise.all(deletePromises);
        }));
      });

      return Promise.all(promises);
    })
    .catch((error) => {
      console.error(error);
      return null;
    });
});
