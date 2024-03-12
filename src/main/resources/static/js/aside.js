/*
 *  Aside menu control
 */
$(document).ready(function() {      // 이벤트 등록
  $('#stateMsgBtn').click(function(e) {
    $('#stateMsgInput').attr({'class': 'mt-2'});    // 입력창이 보이게
    $('#stateInput').val($('#stateMsg').text());    // 입력창에 stateMsg 내용이 보이게
  });
  $('#stateMsgSubmit').click(changeStateMsg);     // 이벤트 등록
  $('#getWeatherButton').click(getWeather);
});

function changeStateMsg() {
  let stateInputVal = $('#stateInput').val();    // 사용자가 수정한 글 읽기
  $('#stateMsgInput').attr({'class': 'mt-2 d-none'});   // 입력창 감추기
  $.ajax({      // Asynchronous Javascript and XML, 화면의 일부분만 바꿀 때 주로 사용
    type: 'GET',
    url: '/abbs/aside/stateMsg',
    data: {stateMsg: stateInputVal},
    success: function(result) {
      console.log('state message:', stateInputVal, result);
      $('#stateMsg').html(stateInputVal);
    }
  });
}

function getWeather() {
  $.ajax({
    type: 'GET',
    url: '/abbs/aside/weather',
    success: function(result) {
      $('#weather').html(result);
    }
  });
}