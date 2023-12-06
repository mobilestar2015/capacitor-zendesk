import Foundation
import ZendeskSDKMessaging
import ZendeskSDK

@objc public class ZendeskImpl: NSObject {
    var presentedViewController: UINavigationController? = nil

    @objc public func initialize(_ channelKey: String) {
        Zendesk.initialize(withChannelKey: channelKey,
                           messagingFactory: DefaultMessagingFactory()) { result in
                if case let .failure(error) = result {
                    print("Messaging did not initialize.\nError: \(error.localizedDescription)")
                }
            }
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
