var stompClient = null;
var auctionId = 68;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

var token = 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtLmphZmFyaTk4NzdAZ21haWwuY29tIiwiZXhwIjoxNTY2OTkwMTI1LCJpYXQiOjE1NjY5NzIxMjV9.HOlQ9Eykg--AUPlbggsKS_hmkkAkkTBqwE8JLRgc7L-5fCKWKAgeoWm-w5S8x7njmSCTmdetz2sdTSx-WdixZg'
function connect() {
    var socket = new SockJS('/ws');
    // var url = "ws://localhost:8080/test-websocket";
    // var stompClient = Stomp.client(url);
    stompClient = /*StompJs.*/Stomp.over(/*function() {return */socket/*}*/);
    stompClient.connect({'Authorization': token}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame.body);
        // stompClient.subscribe('/auctions/'+auctionId+'/bookmark', function (greeting) {
        //     showGreeting(greeting.body);
        // });
        // stompClient.subscribe('/home/auctions/'+auctionId, function (greeting) {
        //     showGreeting(greeting.body);
        // });
        stompClient.subscribe('/user/auction/'+auctionId, function (greeting) {
            showGreeting(greeting.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {

    // stompClient.send("/app/auctions/bookmark", {}, JSON.stringify({'name': $("#name").val()}));
    // stompClient.send("/app/auctions/bookmark", {'Authorization': token}, JSON.stringify({'auctionId': 57}));
    // stompClient.send("/app/enter", {'Authorization': token}, JSON.stringify({'auctionId': 49, 'bidPrice': 10000}));
    stompClient.send("/app/enter", {'Authorization': token}, JSON.stringify({'auctionId': auctionId}));
    stompClient.send("/app/bid", {'Authorization': token}, JSON.stringify({'auctionId': auctionId, 'bidPrice': 1}));
    // stompClient.send("/app/bid", {'Authorization': token}, JSON.stringify({'auctionId': auctionId, 'bidPrice': 2}));
    // stompClient.send("/app/bid", {'Authorization': token}, JSON.stringify({'auctionId': auctionId, 'bidPrice': 3}));
    // stompClient.send("/app/bid", {'Authorization': token}, JSON.stringify({'auctionId': auctionId, 'bidPrice': 4}));
    // stompClient.send("/app/bid", {'Authorization': token}, JSON.stringify({'auctionId': auctionId, 'bidPrice': 5}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    // connect();
    $( "#disconnect" ).click(function() { disconnect(); });
    // sendName();
    $( "#send" ).click(function() { sendName(); });
});