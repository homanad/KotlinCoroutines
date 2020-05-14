# Coroutines

## What is Coroutines

* Thông thường, có 2 loại multitasking methods để quản lí multiple processes:
  * OS quản lí việc chuyển đổi giữa các processes
  * "Cooperative Multitasking", mỗi processes quản lý behavior của chính nó

* Coroutines là một software components tạo ra các sub coroutines cho Cooperative 	Multitasking
* Coroutines được sử dụng lần đầu tiên vào năm 1958 cho assembly language; python, javascript, c# cũng đã sử dụng coroutines trong nhiều năm.
* Trong Kotlin, coroutines được giới thiệu như một sequence of well managed sub tasks. Ở mức độ nào đó, coroutine có thể được xem như một Thread gọn nhẹ
* Một thread có thể chạy nhiều coroutines, một coroutine cũng có thể được chuyển qua lại giữa các thread, có thể suspend ở một thread và resume ở một thread khác.


##  Why we need Coroutines

* Tất cả những "painful task" được thực hiện bằng RxJava, AsyncTask hoặc những methods khác như executors, HandlerThreads và IntentServices đều được thực hiện một cách đơn giản bằng coroutines
* Coroutines API cũng cho phép viết asynchronous code trong một sequential manner.
* Tránh những unneccessary boilerplate code đến từ các callbacks và làm cho code readable và maintainable.

##  Why we need asynchronous programming in android development?
* Most of the smartphones có refresh frequency thấp nhất là 60Hz. Điều này có nghĩa là ứng dụng sẽ refresh 60 lần mỗi giây (16.666ms cho mỗi lần refresh). Do đó, nếu ta chạy một ứng dụng mà nó sẽ vẽ trên màn hình bằng main thread mỗi 16.66s. ĐỒng thời, cũng có những smartphones trên thị trường có refresh frequency là 90Hz hoặc 120Hz, tượng tự, ứng dụng chỉ cần 11ms và 8ms để thực hiện refresh trên main thread.
* Mặc định, android main thread sẽ có một bộ các regular responsibilities - nó sẽ luôn parse XML, inflate view components và draw chúng lặp lại mỗi lần REFRESH
* Main thread cũng phải lắng nghe các tương tác của người dùng như click events
  * Vì vậy, nếu ta viết quá nhiều task xử lý trên main thread, nếu thời gian thực thi của nó vượt quá thời gian siêu nhỏ giữa các lần refresh, ứng dụng sẽ cho thấy performance errors, freeze the screen, unpredictable behaviours.
  * Như một kết quả của công nghệ, refresh frequency sẽ ngày càng cao, ta cần phải triển khai những tác vụ bất đồng bộ cần thời gian chạy dài trong một luồng riêng. Để đạt được điều đó, cách mới nhất, hiệu quả nhất chúng ta có hôm nay chính là Kotlin coroutines.
	
-- Demo "WhyCoroutines" --

## Coroutines vs. Thread
* Coroutine và Thread có giôngs nhau? - KHÔNG
* Chúng ta có Main thread (hay còn gọi là UI Thread), ngoài ra chúng ta còn có khác background worker thread, nhưng thread không giống coroutines.
* Bất cứ thread nào cũng có thể có nhiều coroutines được thực hiện trong cùng một lúc.
* Coroutines chỉ là "separate processors" chạy trên một thread, thậm chỉ có thể lên đến 100 coroutines chạy cùng 1 lúc
* Nhưng mặc định, coroutines không giúp ta theo dõi chúng, hoặc theo dõi những công việc được hoàn thành bởi chúng. Do đó nếu ta không quản lý cẩn thận, chúng có thể dẫn tới leak memory. Nhưng những nhà phát triển Kotlin đã sửa được vấn đề này
* Trong Kotlin coroutines, chúng ta phải chạy tất cả coroutines trong một scope, sử dụng properties trong suốt scope, chúng ta có thể dễ dàng theo dõi coroutines, cancel và xử lý errors hoặc exceptions thrown bởi chúng

## New concept
###  CoroutineScope
* CoroutineScope là một interface mà chungs ta sẽ sử dụng để cung cấp một scope cho những coroutines.
* Trong Kotlin coroutines, chúng ta còn có một scope khác là GlobalScope. GlobalScope sử dụng cho việc chạy các top-level coroutines - which are operating on the whole application lifetime.
* Trong android development, chúng ta rất hiếm khi sử dụng GLobalScope
* Cả 2 Scope này đều được mô tả như một reference cho coroutine context

### Context - Dispatchers
* Dispatcher mô tả một loại của thread nơi mà coroutines sẽ được chạy
* Trong Kotlin Android structured concurrency, nó luôn được khuyến khích sử dụng main thread sau đó chuyển xuống background thread
  * Dispatchers.Main: coroutines sẽ được chạy trên main thread (UI thread), chúng ta chỉ sử dụng main dispatcher cho những tác vụ nhỏ, nhẹ và có tác động đến UI như: call to a ui function, call to a suspending function or to get updates from the livedata. Trong structured concurrency, Recommended best practice là khởi chạy coroutines ở main thread và sau đó chuyển sang background thread.
  * Dispatchers.IO: coroutines sẽ được chạy dưới background thread "from a shared pool of on-demand created threads". Chúng ta sẽ dùng IO dispatcher để làm việc với local database, giao tiếp với network và làm việc với files.
  * Dispatchers.Default: được sử dụng cho CPU intensive tasks như sắp xếp một large list, parse một huge JSON file,...
  * Dispatchers.Unconfined: là một dispatcher được sử dụng với GlobalScope, nếu ta sử dụng Unconfined, coroutines sẽ được chạy trên current thread, nhưng nếu như chúng đã suspended hoặc resumed, nó sẽ chạy trên thread mà có suspending function đang chạy. Dispatcher này không được khuyến khích sử dụng cho Android Development.
* Ngoài 4 dispatcher này, coroutines api cũng tạo điều kiện cho chúng ta chuyển đổi từ "executors" thành "dispatchers", cũng như tạo ra các custom dispatcher
* Tổng kết, trong android development, sử dụng phổ biến nhất là Main và IO
### Coroutines Builder

https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-core

* Coroutine builder là một extension function của coroutine scopes, chúng ta có 4 builders chính:
  * launch: sẽ chạy một coroutine mới mà không block current thread, builder này sẽ trả về một instance của Job, thứ này có thể được sử dụng như một reference cho coroutine. Chúng ta có thể sử dụng instance này để theo dõi hoạt động của coroutine và cancel nó. Chúng ta sử dụng launch builder cho những coroutines không có giá trị trả về. builder này trả về một Job instance nhưng không có giá trị "return", chúng ta không thể sử dụng coroutin để calculate và trả về kết quả cuối cùng.
  * async: Nếu ta muốn nhận được kết quả trả về, nên sử dụng async builder, không chỉ vậy, điều chính của async builder là cho phép chạy coroutines một cách song song, async builder cũng sẽ không block current thread (giống launch builder). Builder này sẽ return một instance của Deferred của type của result. Thực tế, deferred interface là một extension của job interface, vì vậy ta có thể sử dụng nó như một Jo và cancel coroutine. Nếu kết quả trả về của chungs ta là một String value. để lấy đưuọc dữ liệu từ một deferred object, chúng ta phải gọi await() function, async cungx là một trong những builder được sử dụng phổ biến nhất
  * produce: sử dụng cho những coroutines mà nó produce a stream of elements. Builder này trả về instance của ReceiveChannel
  * runblocking: Trong Android Development, chúng ta sử dụng runblocking chủ yếu cho testing, builder này sẽ block thread cho đến khi nó được thực thi xong, builder này trả về type T

#### Job
* Giữ thông tin của coroutine, job cung cấp các phương thức như cancel(), join()
  * cancel(): cancel coroutine, điều đặc biệt ở đây là hàm cancel chỉ set lại property isActive = false, nhưng coroutine vẫn tiếp tục chạy, có 2 cách để thực sự dừng coroutine đã bị gọi cancel:
    * check property isActive trước khi thực hiện tác vụ
    * gọi một suspending function bất kì trước khi thực hiện tác vụ, hàm suspending có khả năng check xem coroutine có còn active hay không, nếu không nó sẽ không thực hiện những dòng sau.
  * join(): khi ta gọi join tức là thì coroutine phải chạy xong tiến trình mới tiếp tục
  * khối finally: nếu coroutine bị cancel, nó sẽ tìm tới khối finally để chạy. Ta có thể tận dụng đặc điểm này để đóng hết resource trước khi coroutine bị hủy. Quay lại ví dụ với cancel(), nếu ta đặt 1 suspending function (ví dụ với delay()) trong khối finally, coroutine sẽ dừng ngay tại đây mà không chạy tiếp các dòng sau.
  * NonCancellable coroutine: với withContext suspending function, ta có thể truyền vào context là NonCancellable để khiến nó chạy kể cả đã cancel hay được check lại với một suspending function.

## Switch the thread of a coroutine
--- Demo Switch thread of a coroutine ---

### Suspending functions
* In Kotlin coroutines, whenever a coroutine is suspended, the current thread will stack frame of the function is copied and saved in the memory.
* When the function resumes after completing its task, the stack fram is copied back from where it was saved and starts running again.
* Kotlin coroutines API cung cấp rất nhiều function để giúp ta làm việc với nó đơn giản hơn
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
  * Coroutine có thể gọi cả suspending function và regular function
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
* Với async builder và sử dụng await function thì có thể nhận được kết quả như mong muốn, nhìn qua thì có vẻ như nó chạy như mong muốn, nhưng ở đây vẫn có vấn đề xảy ra. Trong Android, nếu có một error xảy ra trong function, nó sẽ ném ra exception, vì vậy ta có thể catch exception trong những hàm gọi nó và xử lý.
Trong Unstructured Concurrency, dù sử dụng launch hay async builder, chúng ta đều không thể xử lý exception đúng cách. Vì vậy, mặc dù nó có thể chạy đúng trong một số trường hợp, nhưng thực tế khuyến khích không nên sử dụng.

### Structured Concurrency
<!--* Set of language features and best practices introduced for Kotlin coroutines to avoid coroutines leak and manage coroutines productively-->
* Tất cả những vấn đề phát sinh ở Unstructured Concurrency đều có thể dễ dàng giải quyết với coroutineScope function, chú ý ở đây coroutineScope khác CoroutineScope
  * CoroutineScope là một interface
  * coroutineScope là một suspending function cho phép chúng ta tạo ra các child scope trong một phạm vi coroutine nhất định, coroutine scope này đảm bảo sự hoàn thành của tasks khi suspending function trả về kết quả.

--- Demo StructuredConcurrency ---

* Khi sử dụng coroutineScope, nó sẽ đảm bảo hoàn tành tất cả các tasks trong child scope được cung cấp bởi nó trước khi return (ở đây là launch và async)
* Trong ví dụ trước, kết quả ta nhận được là 70, bởi vì vấn đề xảy ra với unstructured concurrency.
* Ở ví dụ này, kết quả phải là 120

* Ví dụ này chính là best recommended practice, khi chúng ta có nhiều coroutines, chúng ta luôn luôn nên start Dispatcher.Main, với CoroutineScope interface, và bên trong suspending function, ta nên sử dụng coroutineScope function để cung cấp child scope
* =>
  * Structured Concurrency sẽ đảm bảo hoàn thành tất cả các tasks chạy bởi coroutines bên trong child scope trước khi suspending function return. Thực tế, trong coroutineScope, nó đợi child coroutines hoàn thành, không chỉ vậy, nó còn có một lợi ích khác. Khi errors xảy ra, exception được ném ra, structured concurrency cũng đảm bảo được việc thống báo đến caller function. Vì vậy ta có thể dễ dàng xử lý, chúng ta cũng có thể sử dụng structured concurrency để cancel chúng nếu cần.
  * Nếu chúng ta cancel toàn bộ child scope, tất cả những gì xảy ra bên trong nó đều bị cancel.
  * Ta cũng có thể cancel coroutine một cách độc lập => next

### Exception trong Coroutines
* Với launch:
  * Khi throw Exception, coroutine sẽ stop và ném ra exception
* Với async:
  * Khác biệt một chút là, coroutine vẫn stop, nhưng exception k được ném ra
  * Là vì exception được đóng gói vào Deferred, nên chỉ khi ta await() thì nó mới được ném ra.

* Nhưng nếu ta chạy cùng lúc 100 coroutines, làm sao để bắt được hết exception?
#### CoroutineExceptionHandler
* CoroutineExceptionHandler được dùng như một generic catch block của tất cả coroutine.
* Exception sẽ được bắt và trả về cho một hàm callback là override handleException(context: CoroutineContext, exception: Throwable)
* Notes:
  * CoroutineExceptionHandler không thể bắt được exception được đóng gói vào Deferred, và coroutine trong khối runBlocking, do đó ta phải tự catch

* Như đã nói, khi gặp exception, coroutine sẽ tìm code trong khối finally để chạy, vậy nếu code trong finally cũng ném exception, thông thường, exception gặp đầu tiên sẽ được ném ra, lúc này các exception trong khối finally sẽ được suppressed, để in tất cả chúng ta có thể gọi exception.getSuppressed()
  * Example: Caught java.io.IOException with suppressed [java.lang.ArithmeticException, java.lang.IndexOutOfBoundsException]

### SupervisorJob
* Thông thường, khi 1 coroutine con xảy ra Exception, tất cả các coroutine con khác cũng sẽ bị stop. Nếu muốn 1 coroutine con có xảy ra Exception thì các coroutine con khác vẫn hoạt động bình thường, ta có thể sử dụng SupervisorJob thay vì Job
* Khi SupervisorJob cancel thì tất cả con của nó sẽ bị cancel
* Ngoài ra, ta cũng có supervisorScope, tác dụng của nó tương tự như SupervisorJob

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