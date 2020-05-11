# Coroutines

## What is Coroutines

* Thông thường, có 2 loại multitasking methods để quản lí multiple processes:
  * OS quản lí việc chuyển đổi giữa các processes
  * "Cooperative Multitasking", mỗi processes quản lý behavior của chính nó

* Coroutines là một software components tạo ra các sub coroutines cho Cooperative Multitasking
* Coroutines được sử dụng lần đầu tiên vào năm 1958 cho assembly language; Python, Javascript, C# cũng đã sử dụng coroutines trong nhiều năm.
* Trong Kotlin, coroutines được giới thiệu như một "Sequence of well managed sub tasks". Ở mức độ nào đó, coroutine có thể được xem như một Thread gọn nhẹ
* Một thread có thể chạy nhiều coroutines, một coroutine cũng có thể được chuyển qua lại giữa các thread, có thể suspend ở một thread và resume ở một thread khác.

##  Why we need Coroutines

* Tất cả những "painful task" được thực hiện bằng RxJava, AsyncTask hoặc những methods khác như executors, HandlerThreads và IntentServices đều được thực hiện một cách đơn giản bằng coroutines.
* Coroutines API cũng cho phép viết asynchronous code trong một sequential manner.
* Tránh những boilerplate code đến từ các callbacks, làm cho code dễ đọc và dễ bảo trì.

##  Why we need asynchronous programming in android development?
* Hầu hết smartphones có refresh frequency thấp nhất là 60Hz. Điều này có nghĩa là ứng dụng sẽ refresh 60 lần mỗi giây (cách 16.666ms cho mỗi lần refresh). Do đó, nếu ta chạy một ứng dụng mà nó sẽ vẽ trên màn hình bằng main thread mỗi 16.66s. Đồng thời, cũng có những smartphones có refresh frequency là 90Hz hoặc 120Hz, tượng tự, ứng dụng chỉ cần 11.11ms và 8.33ms để thực hiện refresh trên main thread.
* Mặc định, android main thread sẽ có một bộ các regular responsibilities - nó sẽ luôn parse XML, inflate view components và draw chúng lặp lại mỗi lần refresh
* Main thread cũng phải lắng nghe các tương tác của người dùng như click events
  * Vì vậy, nếu ta viết quá nhiều task xử lý trên main thread, nếu thời gian thực thi của nó vượt quá thời gian cực nhỏ giữa các lần refresh, ứng dụng sẽ cho thấy performance errors, freeze the screen, unpredictable behaviours.
  * Với công nghệ, refresh frequency sẽ ngày càng cao, ta cần phải triển khai những tác vụ bất đồng bộ cần thời gian chạy dài trong một luồng riêng. Để đạt được điều đó, cách mới nhất, hiệu quả nhất hiện nay chính là Kotlin coroutines.
	
-- Demo "WhyCoroutines" --

## Coroutines vs. Thread
* Coroutine và Thread có giôngs nhau? - KHÔNG
* Chúng ta có Main thread (hay còn gọi là UI Thread), ngoài ra chúng ta còn có khác background worker thread, nhưng thread không giống coroutine.
* Bất cứ thread nào cũng có thể có nhiều coroutines được thực hiện trong cùng một lúc.
* Coroutines chỉ là "separate processors" chạy trên một thread, thậm chỉ có thể lên đến 100 coroutines chạy cùng 1 lúc.
* Nhưng mặc định, coroutines không giúp ta theo dõi chúng, hoặc theo dõi những công việc được hoàn thành bởi chúng. Do đó nếu ta không quản lý cẩn thận, chúng có thể dẫn tới memory leak.
* Trong Kotlin coroutines, chúng ta phải chạy tất cả coroutines trong một scope, sử dụng properties trong suốt scope, chúng ta có thể dễ dàng theo dõi coroutines, cancel và xử lý errors hoặc exceptions được ném ra bởi chúng.

## New concept
###  CoroutineScope
* CoroutineScope là một interface mà chúng ta sẽ sử dụng để cung cấp một scope cho những coroutines.
* Trong Kotlin coroutines, chúng ta còn có một scope khác là GlobalScope. GlobalScope sử dụng cho việc chạy các top-level coroutines - which are operating on the whole application lifetime.
* Trong android development, chúng ta rất hiếm khi sử dụng GlobalScope
* Cả 2 Scope này đều được mô tả như một reference cho coroutine context
### Context - Dispatchers
* Dispatcher mô tả một loại của thread nơi mà coroutines sẽ được chạy
* Trong Kotlin Android structured concurrency, nó luôn được khuyến khích sử dụng main thread sau đó chuyển xuống background thread
  * Dispatchers.Main: coroutines sẽ được chạy trên main thread (UI thread), chúng ta chỉ sử dụng main dispatcher cho những tác vụ nhỏ, nhẹ và có tác động đến UI như: gọi tới một UI function, gọi tới một suspending function để nhận dữ liệu được cập nhật từ LiveData. Trong structured concurrency, lời khuyên tốt nhất là khởi chạy coroutines ở main thread và sau đó chuyển sang background thread.
  * Dispatchers.IO: coroutines sẽ được chạy ở background thread "from a shared pool of on-demand created threads". Chúng ta sẽ dùng IO dispatcher để làm việc với local database, giao tiếp với network và làm việc với files.
  * Dispatchers.Default: được sử dụng cho CPU intensive tasks như sắp xếp một large list, parse một huge JSON file,...
  * Dispatchers.Unconfined: là một dispatcher được sử dụng với GlobalScope, nếu ta sử dụng Unconfined, coroutines sẽ được chạy trên current thread, nhưng nếu như chúng đã suspended hoặc resumed, nó sẽ chạy trên thread mà có suspending function đang chạy. Dispatcher này không được khuyến khích sử dụng cho Android Development.
* Ngoài 4 dispatcher này, coroutines API cũng tạo điều kiện cho chúng ta chuyển đổi từ "executors" thành "dispatchers", cũng như tạo ra các custom dispatcher.
* Tổng kết, trong Android development, sử dụng phổ biến nhất là Main và IO Dispatcher.
### Coroutines Builder

https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-core

* Coroutine builder là một extension function của coroutine scopes, chúng ta có 4 builders chính:
  * launch: sẽ chạy một coroutine mới mà không block current thread, builder này sẽ trả về một instance của Job, thứ này có thể được sử dụng như một reference cho coroutine. Chúng ta có thể sử dụng instance này để theo dõi hoạt động của coroutine và cancel nó. Chúng ta sử dụng launch builder cho những coroutines không có giá trị trả về. builder này trả về một Job instance nhưng không có giá trị "return", chúng ta không thể sử dụng coroutine này để tính toán và trả về kết quả cuối cùng.
  * async: nếu ta muốn nhận được kết quả trả về, nên sử dụng async builder, điều chính của async builder là cho phép chạy coroutines một cách song song, async builder cũng sẽ không block current thread (giống launch builder). Builder này sẽ return một instance của Deferred. Thực tế, Deferred interface là một extension của Job interface, vì vậy ta có thể sử dụng nó như một Job và cancel coroutine. Nếu kết quả trả về của chúng ta là một String value. để lấy được dữ liệu từ một deferred object, chúng ta phải gọi await() function, async cũng là một trong những builder được sử dụng phổ biến nhất.
  * produce: sử dụng cho những coroutines mà nó tạo ra một luồng các elements. Builder này trả về instance của ReceiveChannel.
  * runblocking: Trong Android Development, chúng ta sử dụng runblocking chủ yếu cho testing, builder này sẽ block thread cho đến khi nó được thực thi xong, builder này trả về type T.

## Switch the thread of a coroutine
--- Demo Switch thread of a coroutine ---

### Suspending functions
* Trong Kotlin coroutines, bất cứ khi nào một coroutine suspended, the current thread will stack frame of the function is copied and saved in the memory.
* When the function resumes after completing its task, the stack frame is copied back from where it was saved and starts running again.
* Kotlin coroutines API cung cấp rất nhiều function để giúp ta làm việc với nó đơn giản hơn:
  * withContext():
  * withTimeout():
  * withTimeoutOrNull():
  * join():
  * delay():
  * await():
  * supervisorScope:
  * coroutineScope:
  * and more...
  * Một số thư viện khác như Room hay Retrofit cũng cung cấp những suspending functions để hỗ trợ công việc với coroutines
* Notes:
  * Một suspending function chỉ có thể được gọi trong một suspending function
  * Suspending function được sử dụng như một "label" cho những hàm nặng và tốn nhiều thời gian chạy.
  * Coroutine có thể gọi cả suspending functions và regular functions
  * Suspending function không block thread

## Async & Await
* Example:
  * Task 1: 10s
  * Task 2: 15s
  * Task 3: 8s
  * Task 4: 12s
    * Với synchronous code, chúng ta sẽ phải đợi ít nhất 10 + 15 + 8 + 12 = 45s để nhận được kết quả cuối cùng
    * Nhưng với asynchronous, chúng ta sẽ chỉ phải đợi khoảng 15s là có thể nhận được kết quả.

* Decomposition Parallel, thông thường, để viết được nó sẽ rất phức tạp, khó viết, khó đọc, khó bảo trì
* Nhưng với Kotlin coroutines, chúng ta có thể làm được nó một cách đơn giản.

--- Demo Async - Await

## Unstructured Concurrency vs. Structured Concurrency
* Trong trường hợp ta muốn chạy nhiều coroutines cùng lúc trong một suspending function và nhận về kết qủa, có 2 cách để làm được điều đó, chúng ta gọi là Structured Concurrency và Unstructured Concurrency
### Unstructured Concurrency
* Đây là cách ứng dụng sai
--- Demo StructuredConcurrency (Un)---
* Unstructured Concurrency sẽ không đảm bảo hoàn thành tất cả tasks của suspending function trước khi trả về.
* Ở đây, thực tế thì child coroutines (delay 1000) vẫn chạy, ngay cả sau khi coroutine cha đã hoàn thành (setText), kết quả dẫn tới là những lỗi không đoán trước, đó là với trường hợp sử dụng launch builder như đã demo.
* Với async builder và sử dụng await function thì có thể nhận được kết quả như mong muốn, nhìn qua thì có vẻ như nó chạy đúng, nhưng ở đây vẫn có vấn đề xảy ra. Trong Android, nếu có một error xảy ra trong function, nó sẽ ném ra exception, vì vậy ta có thể catch exception trong những hàm gọi nó và xử lý.
Trong Unstructured Concurrency, dù sử dụng launch hay async builder, chúng ta đều không thể xử lý exception đúng cách. Vì vậy, mặc dù nó có thể chạy đúng trong một số trường hợp, nhưng thực tế không nên sử dụng.

### Structured Concurrency
<!--* Set of language features and best practices introduced for Kotlin coroutines to avoid coroutines leak and manage coroutines productively-->
* Tất cả những vấn đề phát sinh ở Unstructured Concurrency đều có thể dễ dàng giải quyết với coroutineScope function, chú ý ở đây coroutineScope khác CoroutineScope.
  * CoroutineScope là một interface.
  * coroutineScope là một suspending function cho phép chúng ta tạo ra các child scope trong một phạm vi coroutine nhất định, coroutine scope này đảm bảo sự hoàn thành của tasks khi suspending function trả về kết quả.

--- Demo StructuredConcurrency ---

* Khi sử dụng coroutineScope, nó sẽ đảm bảo hoàn tành tất cả các tasks trong child scope được cung cấp bởi nó trước khi return (ở đây là launch và async).
* Trong ví dụ trước, kết quả ta nhận được là 70, bởi vì vấn đề xảy ra với unstructured concurrency.
* Ở ví dụ này, kết quả phải là 120.

* Ví dụ này chính là best recommended practice, khi chúng ta có nhiều coroutines, chúng ta luôn luôn nên start Dispatcher.Main, với CoroutineScope interface, và bên trong suspending function, ta nên sử dụng coroutineScope function để cung cấp child scope.
* Notes:
  * Structured Concurrency sẽ đảm bảo hoàn thành tất cả các tasks chạy bởi coroutines bên trong child scope trước khi suspending function return. Thực tế, trong coroutineScope, nó đợi child coroutines hoàn thành, không chỉ vậy, nó còn có một lợi ích khác. Khi errors xảy ra, exception được ném ra, structured concurrency cũng đảm bảo được việc thống báo đến caller function. Vì vậy ta có thể dễ dàng xử lý, chúng ta cũng có thể sử dụng structured concurrency để cancel chúng nếu cần.
  * Nếu chúng ta cancel toàn bộ child scope, tất cả những gì xảy ra bên trong nó đều bị cancel.
  * Ta cũng có thể cancel coroutine một cách độc lập.

### viewModelScope
* Following Android Architecture Component - MVVM Architecture
* Sử dụng viewModelScope trong ViewModel:
  * Điều này giúp cho bất cứ coroutines nào chạy trong scope này đều được tự động hủy khi ViewModel isCleared mà không cần override onCleared()
  * Điều này cũng thuận lợi khi ta muốn hoàn thành coroutines chỉ khi ViewModel hoạt động.

--- Demo ViewModelScope ---

### lifecycleScope
* Google cũng giới thiệu thêm một scope tiện dụng được gọi là lifecycleScope, một lifecycleScope được định nghĩa cho mỗi Lifecycle object
* Bất cứ coroutines nào chạy trong scope này đều sẽ cancel khi Lifecycle destroyed
* Đôi khi chúng ta cần tạo coroutines trong objects với một lifecycle, như activities hay fragments
* Tất cả coroutines sẽ được cancel tại onDestroy (Activity và Fragment)
* Ở đây, ta có thêm 3 builder mới:
  * launchWhenCreated: khi có những long running task chỉ xảy ra trong lifecycle của activity hoặc fragment, coroutine này sẽ chạy khi activity hoặc fragment created vào lần đầu tiên
  * launchWhenStarted: coroutine này sẽ chạy khi activity hoặc fragment started
  * launchWhenResumed: chạy coroutine ngay khi app is up and running

--- Demo LifecycleScope ---

### Live Data Builder
* block mới này sẽ tự động thực thi khi live data hoạt động, nó tự động quyết định khi nào stop và cancel coroutines bên trong nó dựa trên lifecycle owner.
* Bên trong Live Data building block, ta có thể sử dụng emit() function để set value cho LiveData

--- Demo Livedata Builder ---
