#import "AppDelegate.h"
#import "GeneratedPluginRegistrant.h"
#import <AlibcTradeSDK/AlibcTradeSDK.h>>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  [GeneratedPluginRegistrant registerWithRegistry:self];
  // Override point for customization after application launch.
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

//IOS9.0 系统新的处理openURL 的API
- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
            options:(NSDictionary<NSString *,id> *)options {
     
    if (@available(iOS 9.0, *)) {
        __unused BOOL isHandledByALBBSDK=[[AlibcTradeSDK sharedInstance]
                                          application:application
                                          openURL:url
                                          options:options];
    } else {
        // Fallback on earlier versions
    }//处理其他app跳转到自己的app，如果百川处理过会返回YES
     
    return YES;
}

- (void)scene:(UIScene *)scene openURLContexts:(NSSet<UIOpenURLContext *> *)URLContexts  API_AVAILABLE(ios(13.0)){
    [URLContexts enumerateObjectsUsingBlock:^(UIOpenURLContext * _Nonnull obj,
                                              BOOL * _Nonnull stop) {
        if([[AlibcTradeSDK sharedInstance] application:nil
                                               openURL:obj.URL
                                               options:nil]){
            *stop = YES;
        }
    }];
}

@end
