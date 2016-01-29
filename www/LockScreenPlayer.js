
var eventCallbackList = {};

function checkEvent(event) {// add namespace ?
    if (event in eventCallbackList) {
        return;
    }
    eventCallbackList[event] = [];
}

var LockScreenPlayer = {
    updateInfos: function (infos, successCallback, errorCallback) {
        //console.group("LockScreenPlayer::updateInfos");
        //console.log(infos);
        //console.groupEnd();

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
    },
    on: function (event, callback) {

        var eventList = event.split(' ');
        for (var i = 0; i < eventList.length; ++i) {
            if (eventList[i].length === 0) {
                continue;
            }

            var currentEvent = eventList[i];

            checkEvent(currentEvent);
            eventCallbackList[currentEvent].push(callback);
        }
    },
    once: function (event, callback) {
        var onceCallback = (function () {
            this.off(event, onceCallback);
            callback.apply(callback, arguments);
        }).bind(this);

        this.on(event, onceCallback);
    },
    off: function (event, callback) {
        var eventList = event.split(' ');
        for (var i = 0; i < eventList.length; ++i) {
            if (eventList[i].length === 0) {
                continue;
            }

            var currentEvent = eventList[i];

            if (!(currentEvent in eventCallbackList)) {
                return;
            }

            if (callback) {
                var indexOf = eventCallbackList[currentEvent].indexOf(callback);
                if (indexOf > -1) {
                    eventCallbackList[currentEvent].splice(indexOf, 1);
                }
            } else {
                eventCallbackList[currentEvent].splice(0, eventCallbackList[currentEvent].length);
            }
            // check current trigger ?
        }
    },
    trigger: function (event) {
        if (!(event in eventCallbackList)) {
            return;
        }

        var data = Array.prototype.slice.call(arguments, 1);

        var callbackList = eventCallbackList[event];
        for (var i = 0; i < callbackList.length; ++i) {
            callbackList[i].apply(callbackList[i], data);
        }
    },
    _setEvent: function (event) {
        //console.log(event);
        this.trigger(event.type);
    }
};

LockScreenPlayer.install = function () {
    if (!window.plugins) {
        window.plugins = {};
    }

    window.plugins.LockScreenPlayer = new SocialSharing();
    return window.plugins.LockScreenPlayer;
};

cordova.addConstructor(LockScreenPlayer.install);
