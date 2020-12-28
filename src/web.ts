import { WebPlugin } from "@capacitor/core";
import {
  SignInWithAppleOptions,
  SignInWithApplePlugin,
  SignInWithAppleResponse,
} from "./definitions";

export class SignInWithAppleWeb
  extends WebPlugin
  implements SignInWithApplePlugin {
  constructor() {
    super({
      name: "SignInWithApple",
      platforms: ["web"],
    });
  }

  async authorize(
    options?: SignInWithAppleOptions
  ): Promise<SignInWithAppleResponse> {
    return;
  }
}

const SignInWithApple = new SignInWithAppleWeb();

export { SignInWithApple };

import { registerWebPlugin } from "@capacitor/core";
registerWebPlugin(SignInWithApple);
