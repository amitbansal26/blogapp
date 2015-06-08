'use strict';

describe('Controller: ViewpostCtrl', function () {

  // load the controller's module
  beforeEach(module('clientApp'));

  var ViewpostCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    ViewpostCtrl = $controller('ViewpostCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
