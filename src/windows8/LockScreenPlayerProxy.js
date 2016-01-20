
var LockScreenPlayerPlugin = require('./LockScreenPlayer');

var WinJSVersion = 0;
var systemMediaTransport;
var mediaControls;

// New way of Media Transport
function initWinJS2() {
    WinJSVersion = 2;

    systemMediaTransport = Windows.Media.SystemMediaTransportControls.getForCurrentView();
    systemMediaTransport.isEnabled = false;
    systemMediaTransport.isPlayEnabled = true;
    systemMediaTransport.isPauseEnabled = true;
    systemMediaTransport.isNextEnabled = true;
    systemMediaTransport.isPreviousEnabled = true;
    systemMediaTransport.addEventListener("buttonpressed", onSystemMediaButtonPressed, false);
}

// Old way of Media Transport
function initWinJS1() {
    WinJSVersion = 1;

    mediaControls = Windows.Media.MediaControl;

    // Enable the previous track button
    mediaControls.addEventListener('previoustrackpressed', onMediaControlButtonPressed, false);

    // Enable the next track button
    mediaControls.addEventListener('nexttrackpressed', onMediaControlButtonPressed, false);

    mediaControls.addEventListener('playpressed', onMediaControlButtonPressed, false);
    mediaControls.addEventListener('pausepressed', onMediaControlButtonPressed, false);
    mediaControls.addEventListener('playpausetogglepressed', onMediaControlButtonPressed, false);
}

(function init() {
    if ('SystemMediaTransportControls' in Windows.Media) {
        initWinJS2();
    } else if ('MediaControl' in Windows.Media) {
        initWinJS1();
    }
})();

function onSystemMediaButtonPressed(event) {

    var action = '';
	switch (event.button) {
		case Windows.Media.SystemMediaTransportControlsButton.play:
			action = 'ActionPlay';
			break;
		case Windows.Media.SystemMediaTransportControlsButton.pause:
		    action = 'ActionPause';
			break;
		case Windows.Media.SystemMediaTransportControlsButton.next:
		    action = 'ActionNext';
			break;
		case Windows.Media.SystemMediaTransportControlsButton.previous:
		    action = 'ActionPrev';
			break;
	    default:
	        return;
	}

	LockScreenPlayerPlugin.LockScreenPlayer._setEvent({ type: action });
}

function onMediaControlButtonPressed(event) {
    var action = '';
    switch (event.type) {
        case 'playpressed':
            action = 'ActionPlay';
            break;
        case 'pausepressed':
            action = 'ActionPause';
            break;
        case 'nexttrackpressed':
            action = 'ActionNext';
            break;
        case 'previoustrackpressed':
            action = 'ActionPrev';
            break;
        case 'playpausetogglepressed':
            action = mediaControls.isPlaying ? 'ActionPause' : 'ActionPlay';
            break;
        default:
            return;
    }

    LockScreenPlayerPlugin.LockScreenPlayer._setEvent({ type: action });
}

function reloadMediaInfos2(track) {
    systemMediaTransport.isEnabled = true;

    systemMediaTransport.displayUpdater.clearAll();
    if ('cover' in track && track.cover.length > 0) {
        systemMediaTransport.displayUpdater.thumbnail = Windows.Storage.Streams.RandomAccessStreamReference.createFromUri(
            new Windows.Foundation.Uri('data:image/jpeg;base64,' + track.cover)
        );
    }

    systemMediaTransport.displayUpdater.type = Windows.Media.MediaPlaybackType.music;

    systemMediaTransport.displayUpdater.musicProperties.title = track.title;
    systemMediaTransport.displayUpdater.musicProperties.artist = track.artistName;

    if (albumName in track && track.albumName.length > 0) {
        systemMediaTransport.displayUpdater.musicProperties.albumArtist = track.albumName;
    }
    systemMediaTransport.displayUpdater.update();

    if (track.isPlaying) {
        systemMediaTransport.playbackStatus = Windows.Media.MediaPlaybackStatus.playing;
    } else {
        systemMediaTransport.playbackStatus = Windows.Media.MediaPlaybackStatus.paused;
    }
}

function reloadMediaInfos1(track) {

    if ('cover' in track && track.cover.length > 0) {

        // TODO: find a way to set a image data base64
        //var coverUri = new Windows.Foundation.Uri('data:image/jpeg;base64,' + track.cover);
        //mediaControls.albumArt = coverUri;
    }
    mediaControls.artistName = track.artistName;
    mediaControls.isPlaying = track.isPlaying;
    mediaControls.trackName = track.title;

}

function reloadMediaInfos(track) {

    if (WinJSVersion === 2) {
        reloadMediaInfos2(track);
    } else if (WinJSVersion === 1) {
        reloadMediaInfos1(track);
    }

}

module.exports = {
    updateInfos: function (success, fail, args) {
        try {
			var trackInfo = args[0];
			reloadMediaInfos(trackInfo);
			success();
        } catch(ex) {
            fail(ex);
        }
    },
	removePlayer: function (success, fail, args) {
	    systemMediaTransport.isEnabled = false;
	}
};

//require("cordova/windows8/commandProxy").add("LockScreenPlayer", module.exports);
require("cordova/exec/proxy").add("LockScreenPlayer", module.exports);