# Overview

## What is coroutines?

* Nhắc một chút về Cooperative Multitasking
* Coroutines là một software component tạo ra các sub coroutines cho
  Cooperative Multitasking
* Trong Kotlin, coroutines được giới thiệu như một "sequence of well
  managed sub tasks"
* Ở một mức độ nào đó, coroutine được xem như một thread gọn nhẹ.

## Why we need coroutines?

* Coroutines vs. Threads
  * Concurrency - Parrallel
  * Threads
    * Threads and cores
      * Tưởng tượng 4-cores CPU giống như một nhà máy, mỗi core tương
        ứng với một worker. Trong ngữ cảnh này, ta có 4 worker, đại diện
        cho các lõi riêng của bộ xử lý. Thông thường, toàn bộ process
        được điều khiển bởi Boss - OS, người sẽ giao lệnh cho worker.
      * Threads giôngs như một sequences of commands gửi tới CPU core -
        threads chuyển task tới CPU core.
      * Khi workers làm việc, OS sẽ quản lý toàn bộ các threads và để ý
        tới schedule, như ta biết, OS rất đắt giá, giống như threads,
        cả 2 đều tốn nhiều chi phí và cần nhiều resources. Cơ bản, mỗi
        threads trong JVM chiếm khoảng 1MB bộ nhớ.
  * Coroutines
    * Kotlin coroutines are not managed by the OS
      * Nó là language feature. OS không cần phải quan tâm tới
        coroutine hay lên kế hoạch cho nó. Coroutines sẽ quản lý chính
        nó bằng cooperative multitasking.
      * Tại thời điểm coroutine suspend, Kotlin runtime sẽ tìm tới
        coroutine khác để tiếp tục quá trình thực thi. Điều này giôngs
        như mọi việc mà OS phải làm trước đây có thể thực hiện bằng một
        giám sát viên với mức chi phí rẻ hơn nhiều.
      * Coroutines không giống threads, nó không tốn nhiều memory, chỉ
        vài byte cho mỗi coroutine. Do đó, ta có thể chạy đồng thời rất
        nhiều công việc với chi phí cực nhỏ.
  * Threads vs. Coroutines
    * Thread rất hạn chế, ta biết Thread Pool, nó sẽ hạn chế số Thread
      ở một thời điểm, còn coroutines hầu như là hàng free, ta có thể
      khởi chạy hàng nghìn coroutine cùng lúc. Nó cho phép chạy bất
      đồng bộ trong một cách thức viết mã đồng bộ.
    * Blocking vs. Non-blocking, asynchronous programming
      * Blocking và Non-blocking là cách miêu tả việc cách thực thực
        hiện lệnh của một chương trình
        * Blocking chính là các dòng lệnh được thực hiện một cách tuần
          tự, khi một dòng lệnh ở phía trước chưa hoàn thành thì dòng
          lệnh phía sau sẽ không thể thực thi, cho nên nếu dòng lệnh đó
          thực hiện thao tác với IO, networking thì bản thân nó sẽ trở
          thành vật cản và block các xử lý phía sau.
        * Non-blocking nghĩa là các dòng lệnh không nhất thiết lúc nào
          cũng được thực hiện tuần tự. Nếu dòng lệnh phía sau không cần
          kết quả từ các câu lệnh trước, thì nó hoàn toàn có thể được
          thực hiện ngay sau khi dòng lệnh phía trước được gọi
          (Asynchronous), kèm theo mỗi dongf lệnh ta sẽ có một
          callback, callback là đoạn mã sẽ được thực thi sau khi có kết
          quả trả về từ dòng lệnh không đồng bộ.
        * Example:
          * `launch { delay(1000L) println("World!") } print("Hello,")
            Thread.sleep(2000L)`
            * Ở đây, Thread.sleep() là một blocking, delay() là một
              non-blocking. Thread.sleep() sẽ làm cho thread ngủ hoàn
              toàn, trong khi delay() chỉ tạm dừng nó, cho phép các phần
              còn lại hoạt động bình thường.
        * Coroutines là các tính toán có thể bị đình chỉ mà không chặn
          một luồng.
  * Coroutines vs. Callbacks
    * Trước đây, để sử dụng các task tốn nhiều thời gian thì hầu như ta
      đều sử dụng callback, bằng cách này thì task sẽ được chạy dưới
      background thread, khi task chạy xong thì trả kết quả lên main
      thread để cập nhật UI.
    * Example:
      * `// Slow request with callbacks @UiThread fun
        makeNetworkRequest() { // The slow network request runs on
        another thread slowFetch { result -> // When the result is
        ready, this callback will get the result show(result) } //
        makeNetworkRequest() exits after calling slowFetch without
        waiting for the result }`
    * Sử dụng coroutines để loại bỏ callbacks
      * Callbacks là một cách tốt, tuy nhiên code sẽ nặng và khó đọc,
        khó debug.
      * Kotlin coroutines sẽ chuyển cấu trúc callback thành code tuần
        tự, dễ đọc hơn.
      * Cả callback và coroutines đều cho ta một kết quả giống nhau
      * Keyword suspend để đánh dấu function trở thành coroutine
      * Example:
        * ` // Slow request with coroutines @UiThread suspend fun
          makeNetworkRequest() { // slowFetch is another suspend
          function so instead of // blocking the main thread
          makeNetworkRequest will `suspend` until the result is // ready
          val result = slowFetch() // continue to execute after the
          result is ready show(result) } suspend fun slowFetch():
          SlowResult { ... }`
      * Giữa callback và coroutine thì code của coroutine sẽ dễ đọc
        hơn, ngắn gọn và dễ hiểu hơn dù có cùng chung kết quả. Và nếu
        muốn nhiều task chạy hơn thì chỉ cần viết tiếp, không cần phải
        tạo nhiều callback.

# New concepts

## CoroutineScope - GlobalScope

* CoroutineScope là một interface mà chúng ta sẽ sử dụng để cung cấp
  một scope cho những coroutines.
* Trong Kotlin coroutines, chúng ta còn có một scope khác là
  GlobalScope. GlobalScope sử dụng cho việc chạy các top-level
  coroutines - which are operating on the whole application lifetime.
* Trong android development, chúng ta rất hiếm khi sử dụng GlobalScope
* Cả 2 Scope này đều được mô tả như một reference cho coroutine context

## Context
* Dispatchers
  * Dispatcher mô tả một loại của thread nơi mà coroutines sẽ được chạy
  * Trong Kotlin Android structured concurrency, nó luôn được khuyến
    khích sử dụng main thread sau đó chuyển xuống background thread
    * Dispatchers.Main: coroutines sẽ được chạy trên main thread (UI
      thread), chúng ta chỉ sử dụng main dispatcher cho những tác vụ
      nhỏ, nhẹ và có tác động đến UI như: gọi tới một UI function, gọi
      tới một suspending function để nhận dữ liệu được cập nhật từ
      LiveData. Trong structured concurrency, lời khuyên tốt nhất là
      khởi chạy coroutines ở main thread và sau đó chuyển sang
      background thread.
    * Dispatchers.IO: coroutines sẽ được chạy ở background thread "from
      a shared pool of on-demand created threads". Chúng ta sẽ dùng IO
      dispatcher để làm việc với local database, giao tiếp với network
      và làm việc với files.
    * Dispatchers.Default: được sử dụng cho CPU intensive tasks như sắp
      xếp một large list, parse một huge JSON file,...
    * Dispatchers.Unconfined: là một dispatcher được sử dụng với
      GlobalScope, nếu ta sử dụng Unconfined, coroutines sẽ được chạy
      trên current thread, nhưng nếu như chúng đã suspended hoặc
      resumed, nó sẽ chạy trên thread mà có suspending function đang
      chạy. Dispatcher này không được khuyến khích sử dụng cho Android
      Development.
  * Ngoài 4 dispatcher này, coroutines API cũng tạo điều kiện cho chúng
    ta chuyển đổi từ "executors" thành "dispatchers", cũng như tạo ra
    các custom dispatcher.
  * Trong Android development, sử dụng phổ biến nhất là Main và IO
    Dispatcher.
* Job
  * Giữ thông tin của coroutine, job cung cấp các phương thức như
    cancel(), join()
    * cancel(): cancel coroutine, điều đặc biệt ở đây là hàm cancel chỉ
      set lại property isActive = false, nhưng coroutine vẫn tiếp tục
      chạy, có 2 cách để thực sự dừng coroutine đã bị gọi cancel:
      * check property isActive trước khi thực hiện tác vụ
      * gọi một suspending function bất kì trước khi thực hiện tác vụ,
        hàm suspending có khả năng check xem coroutine có còn active hay
        không, nếu không nó sẽ không thực hiện những dòng sau.
    * join(): khi ta gọi join tức là thì coroutine phải chạy xong tiến
      trình mới tiếp tục
    * khối finally: nếu coroutine bị cancel, nó sẽ tìm tới khối finally
      để chạy. Ta có thể tận dụng đặc điểm này để đóng hết resource
      trước khi coroutine bị hủy. Quay lại ví dụ với cancel(), nếu ta
      đặt 1 suspending function (ví dụ với delay()) trong khối finally,
      coroutine sẽ dừng ngay tại đây mà không chạy tiếp các dòng sau.
    * NonCancellable coroutine: với withContext suspending function, ta
      có thể truyền vào context là NonCancellable để khiến nó chạy kể
      cả đã cancel hay được check lại với một suspending function.

## Deferred
* Deferred là một non-blocking, có thể được hủy bỏ nếu được yêu cầu, về
  cơ bản nó cũng đại diện cho Job coroutine, có chứa giá trị cho một
  công việc tương ứng.
* Sử dụng Deferred cho phép ta kết hợp

## Builders
* Coroutine builder là một extension function của coroutine scopes,
  chúng ta có 4 builders chính:
  * launch: sẽ chạy một coroutine mới mà không block current thread,
    builder này sẽ trả về một instance của Job, thứ này có thể được sử
    dụng như một reference cho coroutine. Chúng ta có thể sử dụng
    instance này để theo dõi hoạt động của coroutine và cancel nó.
    Chúng ta sử dụng launch builder cho những coroutines không có giá
    trị trả về. builder này trả về một Job instance nhưng không có giá
    trị "return", chúng ta không thể sử dụng coroutine này để tính toán
    và trả về kết quả cuối cùng.
  * async: nếu ta muốn nhận được kết quả trả về, nên sử dụng async
    builder, điều chính của async builder là cho phép chạy coroutines
    một cách song song, async builder cũng sẽ không block current thread
    (giống launch builder). Builder này sẽ return một instance của
    Deferred. Thực tế, Deferred interface là một extension của Job
    interface, vì vậy ta có thể sử dụng nó như một Job và cancel
    coroutine. Nếu kết quả trả về của chúng ta là một String value. để
    lấy được dữ liệu từ một deferred object, chúng ta phải gọi await()
    function, async cũng là một trong những builder được sử dụng phổ
    biến nhất.
  * produce: sử dụng cho những coroutines mà nó tạo ra một luồng các
    elements. Builder này trả về instance của ReceiveChannel.
  * runblocking: Trong Android Development, chúng ta sử dụng
    runblocking chủ yếu cho testing, builder này sẽ block thread cho
    đến khi nó được thực thi xong, builder này trả về type T.

## Suspending functions
* Trong Kotlin coroutines, bất cứ khi nào một coroutine suspended, the
  current thread will stack frame of the function is copied and saved in
  the memory.
* When the function resumes after completing its task, the stack frame
  is copied back from where it was saved and starts running again.
* Kotlin coroutines API cung cấp rất nhiều function để giúp ta làm việc
  với nó đơn giản hơn:
  * withContext():
  * withTimeout():
  * withTimeoutOrNull():
  * join():
  * delay():
  * await():
  * supervisorScope:
  * coroutineScope:
  * and more...
  * Một số thư viện khác như Room hay Retrofit cũng cung cấp những
    suspending functions để hỗ trợ công việc với coroutines
* Notes:
  * Một suspending fun
  * Suspending function đ ction chỉ có thể được gọi trong một suspending
    functionược sử dụng như một "label" cho những hàm nặng
    và tốn nhiều thời gian chạy.
  * Coroutine có thể gọi cả suspending functions và regular functions
  * Suspending function không block thread

# Exceptions handling
## Basic catch
* Với launch:
  * Khi throw Exception, coroutine sẽ stop và ném ra exception
* Với async:
  * Khác biệt một chút là, coroutine vẫn stop, nhưng exception k được
    ném ra
  * Là vì exception được đóng gói vào Deferred, nên chỉ khi ta await()
    thì nó mới được ném ra.

* Nhưng nếu ta chạy cùng lúc 100 coroutines, làm sao để bắt được hết
  exception?
## CoroutineExceptionHandler
* CoroutineExceptionHandler được dùng như một generic catch block của
  tất cả coroutine.
* Exception sẽ được bắt và trả về cho một hàm callback là override
  handleException(context: CoroutineContext, exception: Throwable)
* Notes:
  * CoroutineExceptionHandler không thể bắt được exception được đóng
    gói vào Deferred, và coroutine trong khối runBlocking, do đó ta
    phải tự catch

* Như đã nói, khi gặp exception, coroutine sẽ tìm code trong khối
  finally để chạy, vậy nếu code trong finally cũng ném exception, thông
  thường, exception gặp đầu tiên sẽ được ném ra, lúc này các exception
  trong khối finally sẽ được suppressed, để in tất cả chúng ta có thể
  gọi exception.getSuppressed()
  * Example: Caught java.io.IOException with suppressed
    [java.lang.ArithmeticException, java.lang.IndexOutOfBoundsException]

# Become a master
## SupervisorJob - supervisorScope
* Thông thường, khi 1 coroutine con xảy ra Exception, tất cả các
  coroutine con khác cũng sẽ bị stop. Nếu muốn 1 coroutine con có xảy
  ra Exception thì các coroutine con khác vẫn hoạt động bình thường, ta
  có thể sử dụng SupervisorJob thay vì Job
* Khi SupervisorJob cancel thì tất cả con của nó sẽ bị cancel
* Ngoài ra, ta cũng có supervisorScope, tác dụng của nó tương tự như
  SupervisorJob
## viewModelScope
* Following Android Architecture Component - MVVM Architecture
* Sử dụng viewModelScope trong ViewModel:
  * Điều này giúp cho bất cứ coroutines nào chạy trong scope này đều
    được tự động hủy khi ViewModel isCleared mà không cần override
    onCleared()
  * Điều này cũng thuận lợi khi ta muốn hoàn thành coroutines chỉ khi
    ViewModel hoạt động.
## lifecyclerScope
* Google cũng giới thiệu thêm một scope tiện dụng được gọi là
  lifecycleScope, một lifecycleScope được định nghĩa cho mỗi Lifecycle
  object
* Bất cứ coroutines nào chạy trong scope này đều sẽ cancel khi
  Lifecycle destroyed
* Đôi khi chúng ta cần tạo coroutines trong objects với một lifecycle,
  như activities hay fragments
* Tất cả coroutines sẽ được cancel tại onDestroy (Activity và Fragment)
* Ở đây, ta có thêm 3 builder mới:
  * launchWhenCreated: khi có những long running task chỉ xảy ra trong
    lifecycle của activity hoặc fragment, coroutine này sẽ chạy khi
    activity hoặc fragment created vào lần đầu tiên
  * launchWhenStarted: coroutine này sẽ chạy khi activity hoặc fragment
    started
  * launchWhenResumed: chạy coroutine ngay khi app is up and running
## liveData Builder
* block mới này sẽ tự động thực thi khi live data hoạt động, nó tự động
  quyết định khi nào stop và cancel coroutines bên trong nó dựa trên
  lifecycle owner.
* Bên trong Live Data building block, ta có thể sử dụng emit() function
  để set value cho LiveData

# Q&A

