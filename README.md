# Coroutines

## What

* Thông thường, có 2 loại multitasking mothods để quản lí multiple processes:
  * OS quản lí việc chuyển đổi giữa các processes
  * "Cooperative Multitasking", mỗi processes quản lý behavior của chính nó

* Coroutines là một software components tạo ra các sub coroutines cho Cooperative 	Multitasking
* Coroutines được sử dụng lần đầu tiên vào năm 1958 cho assembly language; python, javascript, c# cũng đã sử dụng coroutines trong nhiều năm.
* Trong Kotlin, coroutines được giới thiệu như một sequence of well managed sub tasks. Ở mức độ nào đó, coroutine có thể được xem như một Thread gọn nhẹ
* Một thread có thể chạy nhiều coroutines, một coroutine cũng có thể được chuyển qua lại giữa các thread, có thể suspend ở một thread và resume ở một thread khác.


##  Why
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
* Chúng ta có Main thread (hay còn gọi là UI Thread), ngoài ra chungs ta còn có khác background worker thread, nhưng thread không giống coroutines.
* Bất cứ thread nào cungx có thể có nhiều coroutines được thực hiện trong cùng một lúc.
* Coroutines chỉ là "separate processors" chạy trên một thread, thậm chỉ có thể lên đến 100 coroutines chạy cungf 1 lúc
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
* Trong Kotlin Android structured concurrency, nó luôn được khuyến khích sử dụng main thread sau đó chuyển xuôngs background thread
  * Dispatchers.Main: coroutines sẽ được chạy trên main thread (UI thread), chúng ta chỉ sử dụng main dispatcher cho những tác vụ nhỏ, nhẹ và có tác động đến UI như: call to a ui function, call to a suspending function or to get updates from the livedata. Trong structured concurrency, Recommended best practice là khởi chạy coroutines ở main thread và sau đó chuyển sang background thread.
  * Dispatchers.IO: coroutines sẽ được chạy dưới background thread "from a shared pool of on-demand created threads". Chúng ta sẽ dùng IO dispatcher để làm việc với local database, giao tiếp với network và làm việc với files.
  * Dispatchers.Default: được sử dụng cho CPU intensive tasks như sắp xếp một large list, parse một huge JSON file,...
  * Dispatchers.Unconfined: là một dispatcher được sử dụng với GlobalScope, nếu ta sử dụng Unconfined, coroutines sẽ được chạy trên current thread, nhưng nếu như chúng đã suspended hoặc resumed, nó sẽ chạy trên thread mà có suspending function đang chạy. Dispatcher này không được khuyến khích sử dụng cho Android Development.
* Ngoài 4 dispatcher này, coroutines api cũng tạo điều kiện cho chúng ta chuyển đổi từ "executors" thành dispatchers", cũng như tạo ra các custom dispatcher
* Tổng kết, trong android development, sử dụng phổ biến nhất là Main và IO
### Coroutines Builder

https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-core

* Coroutine builder là một extension function của coroutine scopes, chúng ta có 4 builders chính:
  * launch: sẽ chạy một coroutine mới mà không block current thread, builder này sẽ trả về một instance của Job, thứ này có thể được sử dụng như một reference cho coroutine. Chúng ta có thể sử dụng instance này để theo dõi hoạt động của coroutine và cancel nó. Chúng ta sử dụng launch builder cho những coroutines không có giá trị trả về. builder này trả về một Job instance nhưng không có giá trị "return", chúng ta không thể sử dụng coroutin để calculate và trả về kết quả cuối cùng.
  * async: Nếu ta muốn nhận được kết quả trả về, nên sử dụng async builder, không chỉ vậy, điều chính của async builder là cho phép chạy coroutines một cách song song, async builder cũng sẽ không block current thread (giống launch builder). Builder này sẽ return một instance của Deferred của type của result. Thực tế, deferred interface là một extension của job interface, vì vậy ta có thể sử dụng nó như một Jo và cancel coroutine. Nếu kết quả trả về của chungs ta là một String value. để lấy đưuọc dữ liệu từ một deferred object, chúng ta phải gọi await() function, async cungx là một trong những builder được sử dụng phổ biến nhất
  * produce: sử dụng cho những coroutines mà nó produce a stream of elements. Builder này trả về instance của ReceiveChannel
  * runblocking: Trong Android Development, chúng ta sử dụng runblocking chủ yếu cho testing, builder này sẽ block thread cho đến khi nó được thực thi xong, builder này trả về type T


### Structured Concurrency
* Set of language features and best practices introduced for Kotlin coroutines to avoid coroutines leak and manage coroutines productively

### Unstructured Concurrency


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
