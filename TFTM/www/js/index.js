/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var accessToken = "798f3c94e0ae4083a6949c778af301c3";
var baseUrl = "https://api.api.ai/v1/";

function textEnter() {
    event.preventDefault();
          send();
}

   function onBodyLoad()
    {       
      document.addEventListener("deviceready", onDeviceReady, false);
    }

function messageSubmit() {
  switchRecognition();
}

var recognition;
function startRecognition() {
  recognition = new webkitSpeechRecognition();
  recognition.onstart = function(event) {
    updateRec();
  };
  recognition.onresult = function(event) {
    var text = "";
      for (var i = event.resultIndex; i < event.results.length; ++i) {
        text += event.results[i][0].transcript;
      }
      setInput(text);
    stopRecognition();
  };
  recognition.onend = function() {
    stopRecognition();
  };
  recognition.lang = "en-US";
  recognition.start();
}

function stopRecognition() {
  if (recognition) {
    recognition.stop();
    recognition = null;
  }
  updateRec();
}
function switchRecognition() {
  if (recognition) {
    stopRecognition();
  } else {
    startRecognition();
  }
}

function setInput(text) {
  $('<div class="message message-personal">' + text + '</div>').appendTo($('.mCSB_container')).addClass('new');
  setDate();
  $('.message-input').val(null);
  updateScrollbar();
  send(text);
}
function updateRec() {
    console.log("Update rec1");
  $(".message-submit").text(recognition ? "Stop" : "Speak");
}


function send(text) {
  $.ajax({
    type: "POST",
    url: baseUrl + "query?v=20150910",
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    headers: {
      "Authorization": "Bearer " + accessToken
    },
    data: JSON.stringify({ q: text, lang: "en" }),
    success: function(data) {
      //setResponse(data);
      prepareResponse(JSON.stringify(data, undefined, 2));
    },
    error: function() {
      setResponse("Internal Server Error");
    }
  });
}

function setResponse(val) {
  var d = JSON.parse(JSON.stringify(val, undefined, 2));
  $("#response").text(d.result.fulfillment.speech);
}

function prepareResponse(val) {
  var d = JSON.parse(val);
  console.log(d.result.fulfillment.speech);
  spokenResponse = d.result.fulfillment.speech;
  respond(spokenResponse);
}

function respond(val) {
  var msg = new SpeechSynthesisUtterance();
  var voices = window.speechSynthesis.getVoices();
  msg.voiceURI = "native";
  msg.text = val;
  msg.lang = "en-US";
    window.speechSynthesis.speak(msg);
  
  if ($('.message-input').val() != '') {
    return false;
  }

  $('<div class="message loading new"><figure class="avatar"><img src="http://s3-us-west-2.amazonaws.com/s.cdpn.io/156381/profile/profile-80_4.jpg" /></figure><span></span></div>').appendTo($('.mCSB_container'));
  updateScrollbar();

  setTimeout(function() {
    $('.message.loading').remove();
    $('<div class="message new"><figure class="avatar"><img src="http://s3-us-west-2.amazonaws.com/s.cdpn.io/156381/profile/profile-80_4.jpg" /></figure>' + val + '</div>').appendTo($('.mCSB_container')).addClass('new');
    setDate();
    updateScrollbar();
  }, 100 + (Math.random() * 20) * 10);

}

var $messages = $('.messages-content'),
    d, h, m,
    i = 0;

$(window).load(function() {
  $messages.mCustomScrollbar();
  setTimeout(function() {
    fakeMessage();
  }, 100);
});

function updateScrollbar() {
  $messages.mCustomScrollbar("update").mCustomScrollbar('scrollTo', 'bottom', {
    scrollInertia: 10,
    timeout: 0
  });
}

function setDate(){
  d = new Date()
  if (m != d.getMinutes()) {
    m = d.getMinutes();
    $('<div class="timestamp">' + d.getHours() + ':' + m + '</div>').appendTo($('.message:last'));
  }
}


$(window).on('keydown', function(e) {
  if (e.which == 13) {
    var t = $('.message-input').val();
    setInput(t);
    return false;
  }
})

var Fake = [
  'Hi there, I can help you find your fitness class',
  'Hello, I can help you find your fitness class',
  'I think you want to be fit, I can assist you with that',
  'Go Gators! how can I help you ?',
  'I just completed the iron marathon, do you want to be fit like me ? I can help you with that '
]

function fakeMessage() {
  if ($('.message-input').val() != '') {
    return false;
  }
  $('<div class="message loading new"><figure class="avatar"><img src="http://s3-us-west-2.amazonaws.com/s.cdpn.io/156381/profile/profile-80_4.jpg" /></figure><span></span></div>').appendTo($('.mCSB_container'));
  updateScrollbar();

  setTimeout(function() {
    $('.message.loading').remove();
    $('<div class="message new"><figure class="avatar"><img src="http://s3-us-west-2.amazonaws.com/s.cdpn.io/156381/profile/profile-80_4.jpg" /></figure>' + Fake[Math.floor(Math.random()*Fake.length)] + '</div>').appendTo($('.mCSB_container')).addClass('new');
    setDate();
    updateScrollbar();
  }, 100 + (Math.random() * 20) * 10);

}
