import Foundation
import ZendeskCoreSDK
import SupportProvidersSDK
import AnswerBotProvidersSDK
import ChatProvidersSDK
import CommonUISDK
import ChatSDK
import MessagingSDK
import AnswerBotSDK
import SupportSDK
import MessagingAPI

@objc public class ZendeskImpl: NSObject {
    var presentedViewController: UINavigationController? = nil

    @objc public func initialize(_ appId: String, clientId: String, zendeskUrl: String, accountKey: String?) -> Bool {
        Zendesk.initialize(appId: appId, clientId: clientId, zendeskUrl: zendeskUrl)
        Support.initialize(withZendesk: Zendesk.instance)
        AnswerBot.initialize(withZendesk: Zendesk.instance, support: Support.instance!)

        return initChat(appId: appId, accountKey: accountKey)
    }

    @objc public func initChat(appId: String, accountKey: String?) -> Bool {
        if (accountKey != nil) {
            Chat.initialize(accountKey: accountKey!, appId: appId)
            return true
        } else {
            return false
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

    @objc public func setNotificationToken(deviceToken: String) -> Void {
        Chat.registerPushTokenString(deviceToken)
    }

    @objc public func setIdentity(token: String?, name: String?, email: String?) -> Void {
        if (token != nil) {
            let identity = Identity.createJwt(token: token!)
            Zendesk.instance?.setIdentity(identity)
        } else {
            let identity = Identity.createAnonymous(name: name, email: email)
            Zendesk.instance?.setIdentity(identity)
        }
    }

    @objc public func setVisitorInfo(name: String, email: String, phone: String, department: String?, tags: [String]?) -> Void {
        let config = ChatAPIConfiguration.init()

        config.departmentId = department ?? ""
        config.tags = tags ?? []
        config.visitorInfo = VisitorInfo.init(name: name, email: email, phoneNumber: phone)

        Chat.instance?.configuration = config

        let identity = Identity.createAnonymous(name: name, email: email)
        Zendesk.instance?.setIdentity(identity)
    }

    func parsePreChatFormConfig(_ key: Any?) -> FormFieldStatus {
        if (key == nil) {
            return FormFieldStatus.optional
        }
        if (key as! String == "required") {
            return FormFieldStatus.required
        }
        if (key as! String == "optional") {
            return FormFieldStatus.optional
        }
        if (key as! String == "hidden") {
            return FormFieldStatus.hidden
        }
        return FormFieldStatus.optional
    }

    func preChatFormConfigurationFromConfig(options: [String: Any]) -> ChatFormConfiguration? {
        let nameStatus = parsePreChatFormConfig(options["name"])
        let emailStatus = parsePreChatFormConfig(options["email"])
        let phoneStatus = parsePreChatFormConfig(options["phone"])
        let departmentStatus = parsePreChatFormConfig(options["department"])
        return ChatFormConfiguration.init(name: nameStatus, email: emailStatus, phoneNumber: phoneStatus, department: departmentStatus)
    }

    func getChatConfiguration(options: [String: Any]) -> ChatConfiguration {
        let chatConfiguration = ChatConfiguration()

        if (options["chatMenuActions"] != nil) {
            chatConfiguration.chatMenuActions = options["chatMenuActions"] as! [ChatMenuAction]
        }
        if (options["isChatTranscriptPromptEnabled"] != nil) {
            chatConfiguration.isChatTranscriptPromptEnabled = options["isChatTranscriptPromptEnabled"] as! Bool
        }
        if (options["isPreChatFormEnabled"] != nil) {
            chatConfiguration.isPreChatFormEnabled = options["isPreChatFormEnabled"] as! Bool
        }
        if (options["isOfflineFormEnabled"] != nil) {
            chatConfiguration.isOfflineFormEnabled = options["isOfflineFormEnabled"] as! Bool
        }
        if (options["isAgentAvailabilityEnabled"] != nil) {
            chatConfiguration.isAgentAvailabilityEnabled = options["isAgentAvailabilityEnabled"] as! Bool
        }

        if (chatConfiguration.isPreChatFormEnabled) {
            let formConfig = preChatFormConfigurationFromConfig(options: options)
            if (formConfig != nil) {
                chatConfiguration.preChatFormConfiguration = formConfig!
            }
        }
        return chatConfiguration
    }

    @objc public func setChatConfiguration(options: [String : Any]) -> ChatConfiguration {
        return getChatConfiguration(options: options)
    }

    @objc public func closeButtonClicked() {
        if (presentedViewController != nil) {
            presentedViewController?.dismiss(animated: true)
            presentedViewController = nil
        }
    }

    @objc public func startChat(parentVC: UIViewController, options: [String: Any]) -> Void {
        let messagingConfiguration = MessagingConfiguration()
        messagingConfiguration.isMultilineResponseOptionsEnabled = true

        let botName = options["botName"] ?? "Chat Bot"
        messagingConfiguration.name = botName as! String

        let chatOnly = options["chatOnly"] as? Bool ?? true

        let engines: [Engine]

        if chatOnly == true {
            engines = try! [ChatEngine.engine()]
        } else {
            engines = try! [ChatEngine.engine(), AnswerBotEngine.engine(), SupportEngine.engine()]
        }

        let chatConfiguration = getChatConfiguration(options: options)

        DispatchQueue.main.async {
            let viewController: UIViewController = try! Messaging.instance.buildUI(engines: engines, configs: [messagingConfiguration, chatConfiguration])

            viewController.navigationItem.leftBarButtonItem = UIBarButtonItem.init(title: "Close", style: UIBarButtonItem.Style.plain, target: self, action: #selector(self.closeButtonClicked))

            self.presentViewController(parentVC: parentVC, vc: viewController)
        }
    }

    @objc public func showHelpCenterFunction(parentVC: UIViewController, options: [String: Any]) {
        let answerBotEngine = try! AnswerBotEngine.engine()
        let supportEngine = try! SupportEngine.engine()

        let helpCenterConfiguration = HelpCenterUiConfiguration()
        helpCenterConfiguration.engines = [answerBotEngine, supportEngine]
        helpCenterConfiguration.labels = ["iOS"]

        let articleConfiguration = ArticleUiConfiguration()
        articleConfiguration.engines = [answerBotEngine, supportEngine]

        let messagingConfiguration = MessagingConfiguration()
        messagingConfiguration.name = options["botName"] as! String

        let requestConfiguration = RequestUiConfiguration()

        DispatchQueue.main.async {
            let helpCenter = HelpCenterUi.buildHelpCenterOverviewUi(withConfigs: [helpCenterConfiguration, articleConfiguration, messagingConfiguration, requestConfiguration])

            self.presentViewController(parentVC: parentVC, vc: helpCenter)
        }
    }

    @objc public func presentViewController(parentVC: UIViewController, vc: UIViewController) {
        presentedViewController = UINavigationController.init(rootViewController: vc)
        parentVC.present(presentedViewController!, animated: true, completion: nil)
    }
}
