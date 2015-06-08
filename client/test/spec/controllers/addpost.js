'use strict';

describe('Controller: AddpostCtrl', function () {

  // load the controller's module
  beforeEach(module('clientApp'));

  var AddpostCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    AddpostCtrl = $controller('AddpostCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
