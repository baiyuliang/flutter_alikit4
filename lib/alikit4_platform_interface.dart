import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'alikit4_method_channel.dart';

abstract class Alikit4Platform extends PlatformInterface {
  /// Constructs a Alikit4Platform.
  Alikit4Platform() : super(token: _token);

  static final Object _token = Object();

  static Alikit4Platform _instance = MethodChannelAlikit4();

  /// The default instance of [Alikit4Platform] to use.
  ///
  /// Defaults to [MethodChannelAlikit4].
  static Alikit4Platform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [Alikit4Platform] when
  /// they register themselves.
  static set instance(Alikit4Platform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<Map?> init() {
    throw UnimplementedError('init() has not been implemented.');
  }

  Future<Map?> login() {
    throw UnimplementedError('login() has not been implemented.');
  }

  Future<Map?> logout() {
    throw UnimplementedError('logout() has not been implemented.');
  }

  Future<Map?> getUserInfo() {
    throw UnimplementedError('getUserInfo() has not been implemented.');
  }

  Future<Map?> setChannel(typeName,channelName) {
    throw UnimplementedError('setChannel() has not been implemented.');
  }

  Future<Map?> setISVVersion(version) {
    throw UnimplementedError('setISVVersion() has not been implemented.');
  }

  Future<Map?> openByBizCode(Map params) {
    throw UnimplementedError('openByBizCode() has not been implemented.');
  }

  Future<Map?> openByUrl(Map params) {
    throw UnimplementedError('openByUrl() has not been implemented.');
  }

  Future<Map?> openCart(Map params) {
    throw UnimplementedError('openCart() has not been implemented.');
  }

  Future<Map?> checkSession() {
    throw UnimplementedError('checkSession() has not been implemented.');
  }

  Future<Map?> getUtdid() {
    throw UnimplementedError('getUtdid() has not been implemented.');
  }

  Future<Map?> oauth(url) {
    throw UnimplementedError('oauth() has not been implemented.');
  }
}
