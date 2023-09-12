angular.module('emixApp.controllers')
    .provider('configService', function () {
        var options = null;
        this.config = function (opt) {
            angular.extend(options, opt);
        };
        this.$get = ['httpServices', '$q', function (httpServices, $q) {
            if (options == null) {
                //throw new Error('Config options must be configured');
                var defer = $q.defer();
                NProgress.start();
                $.blockUI();
                httpServices.getConfigs()
                    .then(function (response) {
                        var data = response.data;
                        options = data.returnObject;
                        defer.resolve(options);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
                return defer.promise;
            }
            return options;
        }];
    });
