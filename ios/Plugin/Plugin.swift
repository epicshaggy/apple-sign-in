import Foundation
import Capacitor
import AuthenticationServices

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(SignInWithApple)
public class SignInWithApple: CAPPlugin {
    var call: CAPPluginCall?

    @objc func authorize(_ call: CAPPluginCall) {
        self.call = call

        if #available(iOS 13.0, *) {
            let appleIDProvider = ASAuthorizationAppleIDProvider()
            let request = appleIDProvider.createRequest()
            
            if let scopes = getScopes(from: call) {
                request.requestedScopes = scopes
            }
            
            if call.hasOption("state") {
                request.state = call.getString("state")!
            }
            
            if call.hasOption("nonce") {
                request.nonce = call.getString("nonce")!
            }

            let authorizationController = ASAuthorizationController(authorizationRequests: [request])
            authorizationController.delegate = self
            authorizationController.performRequests()
        } else {
            call.reject("Sign in with Apple is available on iOS 13.0+ only.")
        }
    }
    
    @available(iOS 13.0, *)
    private func getScopes(from call: CAPPluginCall) -> [ASAuthorization.Scope]?{
        var authorizationScopes: [ASAuthorization.Scope] = []
        
        if let scopes = call.getString("scopes"){
            if scopes.lowercased().contains("name") {
                authorizationScopes.append(.fullName)
            }
            
            if scopes.lowercased().contains("email"){
                authorizationScopes.append(.email)
            }
            
            if(scopes.count > 0){
                return authorizationScopes
            }
        }
        return nil
    }
}

@available(iOS 13.0, *)
extension SignInWithApple: ASAuthorizationControllerDelegate {
    public func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        guard let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential else {
            call?.reject("Please, try again.")

            return
        }

        let result = [
            "response": [
                "user": appleIDCredential.user,
                "email": appleIDCredential.email,
                "givenName": appleIDCredential.fullName?.givenName,
                "familyName": appleIDCredential.fullName?.familyName,
                "identityToken": String(data: appleIDCredential.identityToken!, encoding: .utf8),
                "authorizationCode": String(data: appleIDCredential.authorizationCode!, encoding: .utf8)
            ]
        ]

        call?.resolve(result as PluginResultData)
    }

    public func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        call?.reject(error.localizedDescription)
    }
}
