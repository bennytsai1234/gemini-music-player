package com.pulse.music.data.source.smb

import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.SmbConfig
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.DiskShare
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmbClientManager @Inject constructor() {

    private val config = SmbConfig.builder()
        .withMultiProtocolNegotiate(true)
        .build()

    private val client = SMBClient(config)
    
    // Cache connections to reuse TCP sessions
    private val connections = ConcurrentHashMap<String, Connection>()
    private val sessions = ConcurrentHashMap<String, Session>()
    private val mutex = Mutex()

    suspend fun getDiskShare(
        host: String,
        shareName: String,
        auth: AuthenticationContext = AuthenticationContext.guest()
    ): DiskShare = withContext(Dispatchers.IO) {
        mutex.withLock {
            val connection = getOrEstablishConnection(host)
            val session = getOrEstablishSession(connection, host, auth)
            
            try {
                session.connectShare(shareName) as? DiskShare
                    ?: throw IOException("Share $shareName is not a DiskShare or could not be connected")
            } catch (e: Exception) {
                // If connecting share fails, maybe session is stale? 
                // For now, just throw. Complex retry logic can be added later.
                throw IOException("Failed to connect to share $shareName on $host", e)
            }
        }
    }

    private fun getOrEstablishConnection(host: String): Connection {
        val existing = connections[host]
        if (existing != null && existing.isConnected) {
            return existing
        }
        
        // Clean up dead connection
        existing?.close()
        
        try {
            val newConnection = client.connect(host)
            connections[host] = newConnection
            return newConnection
        } catch (e: Exception) {
            throw IOException("Failed to connect to host $host", e)
        }
    }

    private fun getOrEstablishSession(
        connection: Connection,
        host: String,
        auth: AuthenticationContext
    ): Session {
        val key = "$host:${auth.username}:${auth.domain}"
        val existing = sessions[key]
        
        // Check if session is valid (connection matches and is connected)
        if (existing != null) {
            if (existing.connection.isConnected && existing.connection.remoteHostname == connection.remoteHostname) {
                 return existing
            }
             // Session invalid, remove it
            try { existing.close() } catch (_: Exception) {}
            sessions.remove(key)
        }

        try {
            val newSession = connection.authenticate(auth)
            sessions[key] = newSession
            return newSession
        } catch (e: Exception) {
            throw IOException("Failed to authenticate with $host", e)
        }
    }

    fun close() {
        sessions.values.forEach { try { it.close() } catch (_: Exception) {} }
        sessions.clear()
        connections.values.forEach { try { it.close() } catch (_: Exception) {} }
        connections.clear()
        client.close()
    }
}
