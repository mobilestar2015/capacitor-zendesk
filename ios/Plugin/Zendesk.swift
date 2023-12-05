import Foundation
import ZendeskCoreSDK
import SupportProvidersSDK
import AnswerBotProvidersSDK
import ChatProvidersSDK
import CommonUISDK
import ZendeskSDKMessaging
import ZendeskSDK
import MessagingAPI

@objc public class ZendeskImpl: NSObject {
    var presentedViewController: UINavigationController? = nil

    @objc public func initialize(_ channelKey: String?) -> Bool {
        Zendesk.initialize(withChannelKey: channelKey,
                           messagingFactory: DefaultMessagingFactory()) { result in
                if case let .failure(error) = result {
                    print("Messaging did not initialize.\nError: \(error.localizedDescription)")
                }
            }
    }

    @objc public func setPrimaryColor(primaryColor: String) -> Void {
        CommonTheme.currentTheme.primaryColor = colorFromHexString(hexString: primaryColor) ?? .darkGray
    }

    func colorFromHexString(hexString: String) -> UIColor? {
        var hexString = hexString.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()

        if hexString.hasPrefix("#") {
            hexString.remove(at: hexString.startIndex)
        }

        if hexString.count != 6 {
            return nil
        }

        var rgbValue: UInt64 = 0
        Scanner(string: hexString).scanHexInt64(&rgbValue)

        let red = CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0
        let green = CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0
        let blue = CGFloat(rgbValue & 0x0000FF) / 255.0

        return UIColor(red: red, green: green, blue: blue, alpha: 1.0)
    }

    @objc public func startChat(parentVC: UIViewController) -> Void {
        
        DispatchQueue.main.async {
            guard let viewController = Zendesk.instance?.messaging?.messagingViewController() else { return }
            self.presentViewController(parentVC: parentVC, vc: viewController)
        }
    }

    @objc public func presentViewController(parentVC: UIViewController, vc: UIViewController) {
        presentedViewController = UINavigationController.init(rootViewController: vc)
        parentVC.present(presentedViewController!, animated: true, completion: nil)
    }
}
