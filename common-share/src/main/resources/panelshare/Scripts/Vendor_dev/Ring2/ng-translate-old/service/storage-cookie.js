angular.module('pascalprecht.translate')

    /**
     * @ngdoc object
     * @name pascalprecht.translate.$translateCookieStorage
     * @requires $cookieStore
     *
     * @description
     * Abstraction layer for cookieStore. This service is used when telling angular-translate
     * to use cookieStore as storage.
     *
     */
    .factory('$translateCookieStorage', ['$cookieStore', function ($cookieStore) {

        var $translateCookieStorage = {

            /**
             * @ngdoc function
             * @name pascalprecht.translate.$translateCookieStorage#get
             * @methodOf pascalprecht.translate.$translateCookieStorage
             *
             * @description
             * Returns an item from cookieStorage by given name.
             *
             * @param {string} name Item name
             * @return {string} Value of item name
             */
            get: function (name) {
                //MehdiB For Multi Language Error
                //return $cookieStore.get(name);
                return $cookieStore.getString(name);
            },

            //MehdiB For Multi Language Error
            getString: function (name) {
                return $cookieStore.getString(name);
            },

            /**
             * @ngdoc function
             * @name pascalprecht.translate.$translateCookieStorage#set
             * @methodOf pascalprecht.translate.$translateCookieStorage
             *
             * @description
             * Sets an item in cookieStorage by given name.
             *
             * @param {string} name Item name
             * @param {string} value Item value
             */
            set: function (name, value) {
                //MehdiB For Multi Language Error
                //$cookieStore.put(name, value);
                //$cookieStore.putString(name, value);
            }
        };

        return $translateCookieStorage;
    }]);
