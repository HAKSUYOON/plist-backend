package com.zerobase.plistbackend.module.websocket.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zerobase.plistbackend.common.app.exception.ErrorResponse;
import com.zerobase.plistbackend.module.user.entity.User;
import com.zerobase.plistbackend.module.user.model.auth.CustomOAuth2User;
import com.zerobase.plistbackend.module.websocket.domain.VideoSyncManager;
import com.zerobase.plistbackend.module.websocket.dto.request.ChatMessageRequest;
import com.zerobase.plistbackend.module.websocket.dto.request.VideoControlRequest;
import com.zerobase.plistbackend.module.websocket.dto.request.VideoSyncRequest;
import com.zerobase.plistbackend.module.websocket.dto.response.ChatMessageResponse;
import com.zerobase.plistbackend.module.websocket.exception.WebSocketControllerException;
import com.zerobase.plistbackend.module.websocket.service.WebSocketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

  @Mock
  private WebSocketService webSocketService;

  @Mock
  private VideoSyncManager manager;

  @InjectMocks
  private WebSocketController webSocketController;

  @Test
  @DisplayName("채팅 메시지를 보낼 수 있다")
  void testSendMessage() {
    //given
    User user = User.builder()
        .userName("user")
        .userEmail("test@email.com")
        .userImage("testimg.img")
        .build();

    ChatMessageRequest request = ChatMessageRequest.builder()
        .email("test@email.com")
        .message("안녕하세요")
        .build();

    ChatMessageResponse response = ChatMessageResponse
        .from(request, user);

    //when
//    when(webSocketService.sendMessage(request)).thenReturn(response);

    //then
    assertThat(response.getSender()).isEqualTo(user.getUserName());
    assertThat(response.getMessage()).isEqualTo("안녕하세요");
  }

  @Test
  @DisplayName("비디오의 currentTime을 업데이트 한다")
  void testSyncVideo() throws JsonProcessingException {
    //given
    VideoSyncRequest request = VideoSyncRequest.builder()
        .videoId("TestVideoId")
        .playState(1L)
        .currentTime(200L)
        .build();

    Long channelId = 1L;

    final String TYPE = "videoState";
    //when
//    Long currentTime = responseMap.get(TYPE).getCurrentTime();

    //then
  }

  @Test
  @DisplayName("채널의 호스트는 비디오의 재생 및 일시정지를 누를 수 있다")
  void testControlVideo() {
    //given
    VideoControlRequest request = VideoControlRequest.builder()
        .email("TestUser@email.com")
        .videoId("TestVideoId")
        .playState(1L)
        .currentTime(200L)
        .build();
    Long channelId = 1L;

    //when
//    when(webSocketService.isHost(channelId, request.getEmail())).thenReturn(true);
    /*VideoControlResponse response = webSocketController.controlVideo(channelId, request);*/

    //then
    /*assertThat(response.getCurrentTime()).isEqualTo(response.getCurrentTime());*/
  }

  @Test
  @DisplayName("채널의 호스트가 아닐 경우 재생 및 일시정지를 누르면 에러가 발생한다")
  void testControlVideoNotHost() {
    //given
    VideoControlRequest request = VideoControlRequest.builder()
        .videoId("TestVideoId")
        .playState(1L)
        .currentTime(200L)
        .build();
    Long channelId = 1L;

    CustomOAuth2User user = mock(CustomOAuth2User.class);

    //when
    when(webSocketService.isHost(channelId, request.getEmail())).thenReturn(false);

    //then
    WebSocketControllerException exception = assertThrows(WebSocketControllerException.class, () ->
        webSocketController.controlVideo(channelId, request));

    ErrorResponse errorResponse = ErrorResponse.create(exception.getErrorStatus());
    assertThat(errorResponse.getErrorCode()).isEqualTo(exception.getErrorCode());
    assertThat(errorResponse.getErrorType()).isEqualTo(exception.getErrorType());
    assertThat(errorResponse.getMessage()).isEqualTo(exception.getMessage());
  }

//   @Test
//   @DisplayName("채널에 새롭게 들어온 유저는 현재 영상의 currentTime을 받아 영상 시점이 호스트와 같게 동기화 된다")
//   void testSyncVideoForNewUser() {
//       //given
//     VideoSyncRequest request = VideoSyncRequest.builder()
//         .videoId("TestVideoId")
//         .playState(1L)
//         .currentTime(200L)
//         .build();
//     Long channelId = 1L;

//     //when
//     VideoSyncResponse response = webSocketController.syncVideoForNewUser(
//         channelId, request);
//     //the

//     assertThat(request.getCurrentTime()).isEqualTo(response.getCurrentTime());
//   }
}
