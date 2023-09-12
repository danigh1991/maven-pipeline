angular.module('emixApp.directives').directive('ngMapAddress', ['sharedInfo', '$templateRequest', '$rootScope', 'shareFunc', '$compile', '$timeout', '$translate', function (sharedInfo, $templateRequest, $rootScope, shareFunc, $compile, $timeout, $translate) {
    'use strict';

    return {
        scope: {
            options: '=',
            events: '='
        },
        //templateUrl: '/panelshare/directives/map-address/template.html' + $rootScope.version,
        link: function (scope, element, attrs) {

            /*#region Init Options*/
            scope.options.allowMultiMarker = scope.options.allowMultiMarker == true;
            scope.options.mapWidth = scope.options.mapWidth || '100%';
            scope.options.mapHeight = scope.options.mapHeight || '350px';
            scope.options.addressTextAreaStyle = scope.options.addressTextAreaStyle || 'height:100px';
            scope.options.addressTextAreaRowCount = scope.options.addressTextAreaRowCount || 3;
            scope.options.addressTextAreaColCount = scope.options.addressTextAreaColCount || 40;
            scope.options.panelClass = scope.options.panelClass || '';
            scope.options.addressRequired = scope.options.addressRequired == true;
            scope.options.showError = scope.options.showError == true;
            scope.options.address = scope.options.address || '';
            scope.options.markers = scope.options.markers || new Array();
            scope.options.center = scope.options.center || {
                lat: sharedInfo.getDefaultPosLat(),
                lng: sharedInfo.getDefaultPosLan(),
                zoom: sharedInfo.getDefaultMapZoom()
            };
            /*scope.options.center.lat = scope.options.center.lat || sharedInfo.getDefaultMapZoom();
            scope.options.center.lng = scope.options.center.lng || sharedInfo.getDefaultMapZoom();*/
            scope.options.center.zoom = scope.options.center.zoom || sharedInfo.getDefaultMapZoom();
            scope.events = {};
            scope.options.broadcastEvents = {};

            //#region Assign Template
            $templateRequest("/panelshare/directives/map-address/template.html" + $rootScope.version).then(function(html){
                var template = angular.element(html);
                element.append(template);
                $compile(template)(scope);
            });
            //#endregion
            /*#endregion*/

            /*#region Utils*/
            /*#endregion*/

            /*#region Leaflet*/
            scope.$on("leafletDirectiveMap.click", function (event, args) {
                changeMarker(args.leafletEvent.latlng);
                scope.events.onClick && scope.events.onClick(event, args)
            });
            scope.$on("leafletDirectiveMarker.dragend", function (event, args) {
                changeMarker(args.leafletObject._latlng);
                scope.events.onClick && scope.events.onDragEnd(event, args)
            });
            var changeMarker = function (latlng) {
                //scope.options.address = '';
                scope.events.setMarker(latlng.lat, latlng.lng);
            }
            scope.events.setMarker = function (_lat, _lng) {
                var marker = {
                    lat: _lat,
                    lng: _lng,
                    draggable: true
                };
                if(scope.options.allowMultiMarker)
                    scope.options.markers.push(marker);
                else
                    scope.options.markers[0] = marker;
                scope.options.center = {lat: _lat, lng: _lng, zoom: scope.options.center.zoom};
            }
            /*#endregion*/

            /*#region Events*/
            /*#endregion*/
        }
    }

}]);

