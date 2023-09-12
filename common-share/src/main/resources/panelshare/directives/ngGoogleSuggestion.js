angular.module("/google-auto-compelet.tpl.html", []).run(["$templateCache", function ($templateCache) {
    $templateCache.put("/google-auto-compelet.tpl.html",
      '<div class="google-search-sugestion">'
       + '<div id="google-autoContainer" class="tags-cloud">'
            + '<input type="text" ng-attr-placeholder="{{placeholder}}" ng-attr-id="{{searchInputId}}" ng-trim autofocus required'
            +   ' ng-keydown="checkKeyDown($event)" class="form-control" ng-model="searchText" sng-change="search()" ng-blur="clearResults()" />'
            + '<div id="suggestions" class="google-suggestions-list" ng-show=\'suggestions.length > 0\'>'
              + '<div ng-repeat="suggestion in suggestions" class="blockSpan" ng-click="selectSearchTerm($index)" ng-mouseover="$parent.selectedIndex=$index" ng-class="{\'google-selection-active\' : selectedIndex===$index}">{{suggestion}}'
              + '</div>'
            + '</div>'
       + '</div>'
     + '</div>'
     
    );
}]);


angular.module('emixApp.directives').directive('ngGoogleSuggest', ['$http', '$sce',
  function ($http, $sce) {
      'use strict';
      return {
          restrict: 'EA',
          replace: true,
          templateUrl: '/google-auto-compelet.tpl.html',
          scope: {
              searchText: '=ngModel',
              placeholder: '=?',
              searchInputId: '=',
              eMaxlength: '=?',
              //eMinlength: '=',
          },          
          link: function (scope, elem, attrs) {

              scope.suggestions = [];

              scope.selectedTags = [];

              scope.selectedIndex = -1; //currently selected suggestion index

              scope.placeholder = scope.placeholder || "تایپ را شروع کنید";
              //scope.eMinlength = scope.eMinlength || 30;
              scope.eMaxlength = scope.eMaxlength || 60;

              //scope.removeTag = function(index) {
              //  scope.selectedTags.splice(index, 1);
              //}

              scope.clearResults = function () {
                  scope.suggestions = [];
                  console.log('clear results')
              }

              scope.selectSearchTerm = function (index) {
                  if (scope.selectedTags.indexOf(scope.suggestions[index]) === -1) {
                      scope.searchText = scope.suggestions[index];
                      scope.clearResults();
                  }
              }


              scope.search = function () {
                  // If searchText empty, don't search
                  if (scope.searchText == null || scope.searchText.length < 1)
                      return;

                  //var url = 'http://suggestqueries.google.com/complete/search?callback=JSON_CALLBACK&client=firefox&hl=en&q=' + encodeURIComponent(scope.searchText);
                  var url = 'https://suggestqueries.google.com/complete/search';
                  var params = {
                      //callback: 'JSON_CALLBACK',
                      client: 'firefox',
                      hl: 'en',
                      q: scope.searchText//encodeURIComponent(scope.searchText)
                  };
                  $http.defaults.useXDomain = true;

                  //$http(
                  //    {
                  //        url: url,
                  //        method: 'JSONP',
                  //        headers: {
                  //            'Access-Control-Allow-Origin': '*',
                  //            'Access-Control-Allow-Methods': 'POST, GET, OPTIONS, PUT',
                  //            'Content-Type': 'application/json',
                  //            'Accept': 'application/json'
                  //        }
                  //    }
                  //).jsonp(url)
                  $http.jsonp(url, { params: params }).then(function (response) {
                      var data = response.data;

                      //console.log('data');
                      //console.log(data[1]);

                      var results = data[1];
                      if (results.indexOf(scope.searchText) === -1) {
                          data.unshift(scope.searchText);
                      }
                      scope.suggestions = results;
                      scope.selectedIndex = -1;


                  }, function (response) {
                      console.log(response);
                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                  });


              }


              scope.checkKeyDown = function (event) {
                  if (event.keyCode === 13) { //enter pressed
                      scope.selectSearchTerm(scope.selectedIndex);
                  } else if (event.keyCode === 40) { //down key, increment selectedIndex
                      event.preventDefault();
                      if (scope.selectedIndex + 1 !== scope.suggestions.length) {
                          scope.selectedIndex++;
                      }
                  } else if (event.keyCode === 38) { //up key, decrement selectedIndex
                      event.preventDefault();
                      if (scope.selectedIndex - 1 !== -1) {
                          scope.selectedIndex--;
                      }
                  }
                  //else if (scope.searchText && scope.searchText.length >= scope.eMaxlength) {
                  //        event.preventDefault();
                  //        scope.searchText = scope.searchText.substring(0, scope.eMaxlength);
                  //        return;
                  //    }
              };


              scope.checkKeyup = function (event) {
                  console.log('checking keyup');
                  if (event.keyCode === 13 || event.keyCode === 40 || event.keyCode === 38)
                      return;                  
                  scope.search();
              };


              scope.$watch('selectedIndex', function (val) {
                  if (val !== -1) {
                      scope.searchText = scope.suggestions[scope.selectedIndex];
                  }
              });

              elem.bind('blur', scope.clearResults);
              elem.bind('keyup', scope.checkKeyup);

          }


      }
  }
]);