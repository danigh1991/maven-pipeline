angular.module('emixApp.filters').filter('htmlToPlaintext', function () {
        return function (text) {
            //return  text ? String(text).replace(/<[^>]+>/gm, '').replace(/&nbsp;/gi, '') : '';
            return text ? String(text).replace(/<("[^"]*"|'[^']*'|[^'">])*>/gi, '').replace(/^\s+|\s+$/g, '').replace(/&nbsp;/gi, '') : '';
        };
    }
);
angular.module('emixApp.filters').filter("trust", ['$sce', function($sce) {
    return function(htmlCode){
        return $sce.trustAsHtml(htmlCode);
    }
}]);

angular.module('emixApp.filters').filter('listToMatrix', function() {
    function listToMatrix(list, elementsPerSubArray) {
        var matrix = [], i, k;

        for (i = 0, k = -1; i < list.length; i++) {
            if (i % elementsPerSubArray === 0) {
                k++;
                matrix[k] = [];
            }

            matrix[k].push(list[i]);
        }

        return matrix;
    }
    return function(list, elementsPerSubArray) {
        return listToMatrix(list, elementsPerSubArray);
    };
});