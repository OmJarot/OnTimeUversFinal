package com.example.ontimeuvers

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ontimeuvers.repository.UserRepository
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.util.FirebaseUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.ExecutionException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private var userRepository: UserRepository? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        userRepository = UserRepositoryImpl()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.ontimeuvers", appContext.packageName)
    }

    @Test
    fun testUtil() {
        val usersReference = FirebaseUtil.getUsersReference()
        assertNotNull(usersReference)
    }

    @Test
    @Throws(ExecutionException::class, InterruptedException::class)
    fun testFind() {
        val future = userRepository!!.findUserByNimPassword("test", "test")
        val user = future.get()
        assertNotNull(user)
        assertNotNull(user.nim)
    }


}