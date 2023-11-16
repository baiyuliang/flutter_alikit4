
import 'alikit4_platform_interface.dart';

class Alikit4 {
  Future<String?> getPlatformVersion() {
    return Alikit4Platform.instance.getPlatformVersion();
  }

  
  Future<Map?> init()  {
    return Alikit4Platform.instance.init();
  }

  Future<Map?> login()  {
    return Alikit4Platform.instance.login();
  }

  Future<Map?> logout()  {
    return Alikit4Platform.instance.logout();
  }

  Future<Map?> getUserInfo()  {
    return Alikit4Platform.instance.getUserInfo();
  }

  Future<Map?> setChannel(typeName,channelName)  {
    return Alikit4Platform.instance.setChannel(typeName,channelName);
  }

  Future<Map?> setISVVersion(version)  {
    return Alikit4Platform.instance.setISVVersion(version);
  }

  Future<Map?> openByBizCode(Map params)  {
    return Alikit4Platform.instance.openByBizCode(params);
  }

  Future<Map?> openByUrl(Map params)  {
    return Alikit4Platform.instance.openByUrl(params);
  }

  Future<Map?> openCart(Map params)  {
    return Alikit4Platform.instance.openCart(params);
  }

  Future<Map?> checkSession()  {
    return Alikit4Platform.instance.checkSession();
  }

  Future<Map?> getUtdid()  {
    return Alikit4Platform.instance.getUtdid();
  }

  Future<Map?> oauth(url)  {
    return Alikit4Platform.instance.oauth(url);
  }
}
