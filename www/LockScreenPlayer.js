
var LockScreenPlayer = {
	updateInfos: function(infos, successCallback, errorCallback) {
		cordova.exec(
			successCallback,
			errorCallback,
			'LockScreenPlayerPlugin',
			'updateInfos'
			[infos]
		);
	}
};
