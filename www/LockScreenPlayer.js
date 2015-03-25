
exports.LockScreenPlayer = {
    updateInfos: function (infos, successCallback, errorCallback) {
        console.group("LockScreenPlayer::updateInfos");
        console.log(infos);
        console.groupEnd();

		cordova.exec(
			successCallback,
			errorCallback,
			'LockScreenPlayer',
			'updateInfos',
			[infos]
		);
    },
    removePlayer: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'LockScreenPlayer',
            'removePlayer',
			[]
        )
    }
};
