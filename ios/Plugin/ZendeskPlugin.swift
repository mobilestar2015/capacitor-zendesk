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
        let channelKey = call.getString("key") ?? ""

        let success = implementation.initialize(channelKey)

        call.resolve()
    }

    @objc func startChat(_ call: CAPPluginCall) {
        if let vc = self.bridge?.viewController {
            implementation.startChat(parentVC: vc)
            call.resolve()
        } else {
            call.reject("No view controller available")
        }
    }
}
