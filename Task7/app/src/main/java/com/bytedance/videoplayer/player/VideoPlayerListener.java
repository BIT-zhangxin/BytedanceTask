package com.bytedance.videoplayer.player;

import tv.danmaku.ijk.media.player.IMediaPlayer;

interface VideoPlayerListener extends
    IMediaPlayer.OnBufferingUpdateListener,
    IMediaPlayer.OnCompletionListener,
    IMediaPlayer.OnPreparedListener,
    IMediaPlayer.OnInfoListener,
    IMediaPlayer.OnVideoSizeChangedListener,
    IMediaPlayer.OnErrorListener,
    IMediaPlayer.OnSeekCompleteListener {

}
