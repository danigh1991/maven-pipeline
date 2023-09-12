angular.module('pascalprecht.translate')
/**
 * @ngdoc object
 * @name pascalprecht.translate.$translateStaticFilesLoader
 * @requires $q
 * @requires $http
 *
 * @description
 * Creates a loading function for a typical static file url pattern:
 * "lang-en_US.json", "lang-de_DE.json", etc. Using this builder,
 * the response of these urls must be an object of key-value pairs.
 *
 * @param {object} options Options object, which gets prefix, suffix and key.
 */
.factory('$translateStaticFilesLoader', ['$q', '$http', '$timeout', function ($q, $http, $timeout) {

  return function (options) {

    if (!options || (!angular.isString(options.prefix) || !angular.isString(options.suffix))) {
      throw new Error('Couldn\'t load static files, no prefix or suffix specified!');
    }

    var deferred = $q.defer();

    $http(angular.extend({
      url: [
        options.prefix,
        options.key,
        options.suffix
      ].join(''),
      method: 'GET',
      params: ''
    }, options.$http)).then(function (data) {
        /*$timeout(function() {
            deferred.resolve(data.data);
        },1500);*/
        deferred.resolve(data.data);
    }, function (data) {
      deferred.reject(options.key);
    });

    return deferred.promise;
  };
}]);
