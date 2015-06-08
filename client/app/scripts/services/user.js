'use strict';

/**
 * @ngdoc service
 * @name clientApp.user
 * @description
 * # user
 * Service in the clientApp.
 */
angular.module('clientApp')
  .factory('userService', function($http) {
     var username = '';

     return {
       username : username
     };

   });
