import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ZendeskPlugin)
public class ZendeskPlugin: CAPPlugin {
    private let implementation = ZendeskImpl()

    @objc func initialize(_ call: CAPPluginCall) {
        let appId = call.getString("appId") ?? ""
        let clientId = call.getString("clientId") ?? ""
        let zendeskUrl = call.getString("url") ?? ""
        let accountKey = call.getString("key") ?? ""
        
        let success = implementation.initialize(appId, clientId: clientId, zendeskUrl: zendeskUrl, accountKey: accountKey)
        
        call.resolve([
            "success": success
        ])
    }
    
    @objc func initChat(_ call: CAPPluginCall) {
        let appId = call.getString("appId") ?? ""
        let accountKey = call.getString("key")
        let success = implementation.initChat(appId: appId, accountKey: accountKey)
        
        call.resolve([
            "success": success
        ])
    }
    
    @objc func setPrimaryColor(_ call: CAPPluginCall) {
        let colorString = call.getString("primaryColor")!
        implementation.setPrimaryColor(primaryColor: colorString)
        
        call.resolve()
    }
    
    @objc func setNotificationToken(_ call: CAPPluginCall) {
        if let token = call.getString("notificationToken") {
            implementation.setNotificationToken(deviceToken: token)
        } else {
            CAPLog.print("Please provide a notification token")
        }
        
        call.resolve()
    }
    
    @objc func setIdentity(_ call: CAPPluginCall) {
        let token = call.getString("token")
        let name = call.getString("name")
        let email = call.getString("email")
        implementation.setIdentity(token: token, name: name, email: email)
        
        call.resolve()
    }
    
    @objc func setVisitorInfo(_ call: CAPPluginCall) {
        let department = call.getString("department")
        let tags = call.getArray("tags")?.capacitor.replacingNullValues() as? [String]
        let name = call.getString("name")
        let email = call.getString("email")
        let phone = call.getString("phone")
        implementation.setVisitorInfo(name: name!, email: email!, phone: phone!, department: department, tags: tags)
        
        call.resolve()
    }
    
    @objc func setChatConfiguration(_ call: CAPPluginCall) {
        let chatConfiguration = implementation.setChatConfiguration(options: call.options as! [String: Any])
        
        call.resolve([
            "configuration": chatConfiguration
        ])
    }
    
    @objc func startChat(_ call: CAPPluginCall) {
        if let vc = self.bridge?.viewController {
            implementation.startChat(parentVC: vc, options: call.options as! [String: Any])
            call.resolve()
        } else {
            call.reject("No view controller available")
        }
    }
    
    @objc func showHelpCenter(_ call: CAPPluginCall) {
        if let vc = self.bridge?.viewController {
            implementation.showHelpCenterFunction(parentVC: vc, options: call.options as! [String: Any])
            call.resolve()
        } else {
            call.reject("No view controller available")
        }
    }
}
