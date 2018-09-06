
var login_name = parent.document.getElementById('login_name').value;

var app = angular.module('vacApp', []);
app.controller('process2', function ($scope, $http, $window) {
    $scope.defineKey = "";
    $scope.params = "";
    $scope.applyProject = function () {
    	var data = {
    			loginUserName: login_name,
    			"processDefineKey": $scope.defineKey,
    			"params": $scope.params
    		}
//    	$.post('/process/startProcess', data);
    	
//        $http.post(
//            "/process/startProcess",
//            {
//            	"loginUserName": login_name,
//                "processDefineKey" : $scope.defineKey,
//                "params":$scope.params
//            }
//        ).then(function (response) {
//        	alert(response);
//            if (response.data.success == true) {
//                alert("操作成功！");
//                $window.location.reload();
//            }
//        })
    }
}).config(function ($httpProvider) {
	$httpProvider.defaults.transformRequest = function (obj) {
        var str = [];
        for (var p in obj) {
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        }
        return str.join("&");
    };
    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=utf-8';
});
app.controller('myVacCtrl', function ($scope, $http) {

    $scope.vacList = [];

    $scope.myVac = function () {
        $http.get(
            "/myVac"
        ).then(function (response) {
            $scope.vacList = response.data;
        })
    }
});

app.controller('process', function ($scope, $http) {

    $scope.processList = [];

    $scope.listProcessDefinition = function () {
       $http.post(
            "/process/listProcessDefinition?pageNum=1&pageSize=10"
        ).then(function (response) {
            $scope.processList = response.data.content.list;
        })
    }
});

app.controller('process3', function ($scope, $http) {

    $scope.todoList = [];
  /*  $scope.user = '';*/
   /* $scope.options = [{
    	name: '用户2',
    	value: 'user2'
    }, {
    	name: '用户3',
    	value: 'user3'
    }];*/
    $scope.passAudit = function (taskId) {
    	$http.post(
                "/process/commitTasks",
                {
                	"taskId":taskId,
                	"loginUserId":"gm"
                }
            ).then(function (response) {
            	alert(response.data.message);
                /*$scope.todoList = response.data.content.list;*/
            })
    };
    $scope.getTodoTasks = function () {
        $http.post(
            "/process/getTodoTasks?assignee="+login_name
        ).then(function (response) {
            $scope.todoList = response.data.content.list;
        })
    }
});
app.controller('process4', function ($scope, $http) {

    $scope.doneList = [];

    $scope.getDoneTasks = function () {
        $http.post(
            "/process/getDoneTasks?assignee="+login_name
        ).then(function (response) {
            $scope.doneList = response.data.content.list;
        })
    }
});
app.controller('process5', function ($scope, $http) {

    $scope.myProcesses = [];
    $scope.processInsId ="";
    $scope.myProcess = function () {
        $http.post(
            "/process/queryMyProcesses?userId=liulin"
        ).then(function (response) {
            $scope.myProcesses = response.data.content.list;
        })
    }
    $scope.viewImg = function (proInsId) {
        /*$http.post(
            "/process/viewImage",
            {
            	"processInstanceId":proInsId
            }
        ).then(function (response) {
            $scope.myProcesses = response.data.content.list;
        })*/
    	 $scope.processInsId=proInsId;
    	 window.location.href='http://localhost/process/viewImage?processInstanceId='+proInsId
    	// window.open('../pages/processImg.html', '_blank', 'height=400, width=800, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no') 
    }
});
app.controller('process6', function ($scope, $http) {

    $scope.myFinishedProcesses = [];

    $scope.finishedProcess = function () {
        $http.post(
            "/process/queryMyfinishedProcesses"
        ).then(function (response) {
            $scope.myFinishedProcesses = response.data.content.list;
        })
    }
});
app.controller("myAudit", function ($scope, $http, $window) {
    $scope.vacTaskList = [];

    $scope.myAudit = function () {
        $http.get(
            "/myAudit"
        ).then(function (response) {
            $scope.vacTaskList = response.data;
        })
    };

    $scope.passAudit = function (taskId, result) {
        $http.post(
            "/passAudit",
            {
                "id" : taskId,
                "vac" : {
                    "result" : result >= 1 ? "审核通过" : "审核拒绝"
                }
            }
        ).then(function (response) {
            if (response.data == true) {
                alert("操作成功！");
                $window.location.reload();
            } else {
                alert("操作失败！");
            }
        })
    }
});

app.controller('myAuditRecord', function ($scope, $http) {

    $scope.auditVacRecordList = [];

    $scope.myAuditRecord = function () {
        $http.get(
            "/myAuditRecord"
        ).then(function (response) {
            $scope.auditVacRecordList = response.data;
        })
    }
});



