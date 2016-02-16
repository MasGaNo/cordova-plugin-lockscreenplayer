/*

Add to MainViewController.h: 
@property (nonatomic, weak) CDVPlugin *remoteControlPlugin;

Add to MainViewController.m:
- (void)remoteControlReceivedWithEvent:(UIEvent *)event
{
	if ([self.remoteControlPlugin respondsToSelector:@selector(remoteControlReceivedWithEvent:)]) {
		[self.remoteControlPlugin performSelector:@selector(remoteControlReceivedWithEvent:) withObject:event];
	}
}

*/

#import "LockScreenPlayer.h"

@implementation LockScreenPlayer

- (void)pluginInitialize
{
	MainViewController* mainController = (MainViewController*)self.viewController;
	mainController.remoteControlPlugin = self;
	[mainController canBecomeFirstResponder];

	[[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
}

-(void)onReset
{
	[[UIApplication sharedApplication] endReceivingRemoteControlEvents];
}

-(void)updateInfos:(CDVInvokedUrlCommand*)command
{
	CDVPluginResult* pluginResult = nil;
	NSDictionary* json = [command.arguments objectAtIndex : 0];

	NSError* error;
	/*NSDictionary* json = [NSJSONSerialization
	JSONObjectWithData:data

	options:kNilOptions
	error:&error];
	*/
	NSString* title = [json objectForKey : @"title"];
	NSString* artistName = [json objectForKey : @"artistName"];
	NSString* albumName = [json objectForKey : @"albumName"];
	NSString* cover = [json objectForKey : @"cover"];
	NSNumber* duration = [json objectForKey : @"duration"];
	int durationNumber = [duration intValue] / 1000;
	BOOL isPlaying = [[json objectForKey : @"isPlaying"] boolValue];

	//dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{

	//UIImage *cover = nil;
	//cover = [UIImage imageNamed:@"no-image"];

	//CGImageRef cgref = [cover CGImage];
	//CIImage *cim = [cover CIImage];

	//if (cim != nil || cgref != NULL) {
	//dispatch_async(dispatch_get_main_queue(), ^{
	if (NSClassFromString(@"MPNowPlayingInfoCenter")) {
		//MPMediaItemArtwork *artwork = [[MPMediaItemArtwork alloc] initWithImage: cover];
		MPNowPlayingInfoCenter *center = [MPNowPlayingInfoCenter defaultCenter];
		NSMutableDictionary* info = [NSMutableDictionary dictionaryWithCapacity : 6];
		[info setObject : artistName forKey : MPMediaItemPropertyArtist];
		[info setObject : title forKey : MPMediaItemPropertyTitle];
		[info setObject : albumName forKey : MPMediaItemPropertyAlbumTitle];
		[info setObject : [NSNumber numberWithInt : durationNumber] forKey : MPMediaItemPropertyPlaybackDuration];
		//[info setObject:@"40" forKey:MPNowPlayingInfoPropertyElapsedPlaybackTime];
		[center setNowPlayingInfo : info];
		/*center.nowPlayingInfo = [NSMutableDictionary dictionaryWithObjectsAndKeys:
		artistName, MPMediaItemPropertyArtist,
		title, MPMediaItemPropertyTitle,
		albumName, MPMediaItemPropertyAlbumTitle,
		//artwork, MPMediaItemPropertyArtwork,
		duration, MPMediaItemPropertyPlaybackDuration,
		40, MPNowPlayingInfoPropertyElapsedPlaybackTime,
		[NSNumber numberWithInt:1], MPNowPlayingInfoPropertyPlaybackRate, nil];*/
	}
	//});
	//}

	//});

	pluginResult = [CDVPluginResult resultWithStatus : CDVCommandStatus_OK messageAsString : @"Ok..."];
	//pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];

	[self.commandDelegate sendPluginResult : pluginResult callbackId : command.callbackId];
}

-(void)removePlayer:(CDVInvokedUrlCommand*)command
{
	CDVPluginResult* pluginResult = nil;

	[[UIApplication sharedApplication] endReceivingRemoteControlEvents];

	pluginResult = [CDVPluginResult resultWithStatus : CDVCommandStatus_OK messageAsString : @"Ok"];
	//pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];

	[self.commandDelegate sendPluginResult : pluginResult callbackId : command.callbackId];
}

-(void)remoteControlReceivedWithEvent:(UIEvent *)receivedEvent
{
	if (receivedEvent.type == UIEventTypeRemoteControl) {

		NSString *action = @"";

		switch (receivedEvent.subtype) {

		case UIEventSubtypeRemoteControlTogglePlayPause:
			action = @"playpause";//check current isPlaying
			break;
		case UIEventSubtypeRemoteControlPlay:
			action = @"ActionPlay";//check current isPlaying
			break;
		case UIEventSubtypeRemoteControlPause:
			action = @"ActionPause";//check current isPlaying
			break;
		case UIEventSubtypeRemoteControlPreviousTrack:
			action = @"ActionPrev";//check current isPlaying
			break;
		case UIEventSubtypeRemoteControlNextTrack:
			action = @"ActionNext";//check current isPlaying
			break;
		default:
			return;
		}

		NSDictionary *dict = @{@"type": action};
		NSData *jsonData = [NSJSONSerialization dataWithJSONObject : dict options : 0 error : nil];
		NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding : NSUTF8StringEncoding];
		NSString *jsStatement = [NSString stringWithFormat : @"cordova.plugins.LockScreenPlayer._setEvent(%@)", jsonString];
		if ([self.webView isKindOfClass:[UIWebView class]]) {
			[(UIWebView*)self.webView stringByEvaluatingJavaScriptFromString : jsStatement];
		}
	}
}

@end
