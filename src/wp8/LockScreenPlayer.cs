using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Runtime.Serialization;
using System.Windows;
using Microsoft.Phone.Shell;
using Microsoft.Phone.BackgroundAudio;
using WPCordovaClassLib.Cordova.JSON;

namespace WPCordovaClassLib.Cordova.Commands
{
    public class LockScreenPlayer : BaseCommand
    {
        //private SystemMediaTransportControls systemMediaTransport; 

        public void updateInfos(string options)
        {
            string[] trackInfo = JsonHelper.Deserialize<string[]>(options);

            try
            {

                //SystemMediaTransportControlsDisplayUpdater displayUpdater = systemMediaTransport.DisplayUpdater;

                /*displayUpdater.ClearAll();*/


                DispatchCommandResult();
            }
            catch (Exception e)
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR));
            }

        }

        public void removePlayer()
        {
            //systemMediaTransport.IsEnabled = false;
            DispatchCommandResult();
        }

        /// <summary>
        /// Occurs when the application is being loaded, and the config.xml has an autoload entry
        /// </summary>    
        public virtual void OnInit() 
        {
            /*systemMediaTransport = SystemMediaTransportControls.GetForCurrentView();
            systemMediaTransport.ButtonPressed += onButtonPressed;
            systemMediaTransport.IsEnabled = false;
            systemMediaTransport.IsPauseEnabled = true;
            systemMediaTransport.IsPlayEnabled = true;
            systemMediaTransport.IsNextEnabled = true;
            systemMediaTransport.IsPreviousEnabled = true; */
        }

        /// <summary> 
        /// This function controls the button events from UVC. 
        /// This code if not run in background process, will not be able to handle button pressed events when app is suspended. 
        /// </summary> 
        /// <param name="sender"></param> 
        /// <param name="args"></param> 
        /*private void onButtonPressed(SystemMediaTransportControls sender, SystemMediaTransportControlsButtonPressedEventArgs args)
        {
            String action = "";
            switch (args.Button)
            {
                case SystemMediaTransportControlsButton.Play:
                    action = "ActionPlay";
                    break;
                case SystemMediaTransportControlsButton.Pause:
                    action = "ActionPause";
                    break;
                case SystemMediaTransportControlsButton.Next:
                    action = "ActionNext";
                    break;
                case SystemMediaTransportControlsButton.Previous:
                    action = "ActionPrev";
                    break;
                default:
                    return;
            }

        } */

         /// <summary>
        /// Occurs when the application is being deactivated.
        /// </summary>        
        public override void OnReset()
        {
            //See to change player status
            //systemMediaTransport.ButtonPressed -= systemmediatransportcontrol_ButtonPressed;
        }

        /// <summary>
        /// Occurs when the application is being deactivated.
        /// </summary>        
        public override void OnPause(object sender, DeactivatedEventArgs e) 
        {
            //See to change player status
        }

        /// <summary>
        /// Occurs when the application is being made active after previously being put
        /// into a dormant state or tombstoned.
        /// </summary>        
        public override void OnResume(object sender, Microsoft.Phone.Shell.ActivatedEventArgs e) 
        {
            //See to change player status
        }
    }
}