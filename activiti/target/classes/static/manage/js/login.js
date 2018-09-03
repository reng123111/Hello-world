var app = angular.module('vacApp', []);
app.controller('loginCtrl', function ($scope, $http) {
    $scope.userName = "empa";
    $scope.password = "123456";

    $scope.login = function () {
        $http.post(
            "/sdpflowable/login",
            {
                "userName": $scope.userName,
                "password": $scope.password
            }
        ).then(function (response) {
            if (response.data == true) {
                window.location = "/sdpflowable/static/public/pages/index.html";
            	//window.location = "/templates/index.html";
            }
        })
        // $http({
        //     url: "/login",
        //     method: "post",
        //     params: {
        //         "userName": $scope.userName,
        //         "password": $scope.password
        //     }
        // }).then(function (response) {
        //
        // })
    }

});