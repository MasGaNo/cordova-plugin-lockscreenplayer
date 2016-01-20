#import <Cordova/CDV.h>
//#import <Cordova/CDVPlugin.h>
#import <MediaPlayer/MediaPlayer.h>
#import <MediaPlayer/MPNowPlayingInfoCenter.h>
#import <MediaPlayer/MPMediaItem.h>
#import "MainViewController.h"

@interface LockScreenPlayer : CDVPlugin

- (void)updateInfos : (CDVInvokedUrlCommand*)command;
-(void)removePlayer:(CDVInvokedUrlCommand*)command;
-(void)remoteControlReceivedWithEvent:(UIEvent *)receivedEvent;

@end