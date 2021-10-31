package com.andriiginting.crossfademusic.domain

import com.andriiginting.crossfademusic.data.CrossFadeRepository
import io.reactivex.Single
import javax.inject.Inject

interface CrossFadeUseCase {
    fun getPlaylist(): Single<List<MusicPlaylist>>
}

class CrossFadeUseCaseImpl @Inject constructor(private val repository: CrossFadeRepository) : CrossFadeUseCase {

    override fun getPlaylist(): Single<List<MusicPlaylist>> {
        return repository.getPlaylist()
            .map { response ->
                val list = mutableListOf<MusicPlaylist>()
                response.playlist.forEachIndexed { index, music ->
                    list.add(
                        MusicPlaylist(
                            id = index.toLong(),
                            track = music.track,
                            musicUrl = music.streamUrl,
                            artist = music.artist,
                            coverUrl = music.coverUrl
                        )
                    )
                }
                list
            }
    }
}