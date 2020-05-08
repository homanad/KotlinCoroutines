# Coroutines

-----What
	Thông thường, có 2 loại multitasking mothods để quản lí multiple processes:
		+ OS quản lí việc chuyển đổi giữa các processes
		+ "Cooperative Multitasking", mỗi processes quản lý behavior của chính nó 

	Coroutines là một software components tạo ra các sub coroutines cho Cooperative 	Multitasking
	Coroutines được sử dụng lần đầu tiên vào năm 1958 cho assembly language; python, javascript, c# cũng đã sử dụng coroutines trong nhiều năm.

	Trong Kotlin, coroutines được giới thiệu như một sequence of well managed sub tasks. Ở mức độ nào đó, coroutine có thể được xem như một Thread gọn nhẹ

	Một thread có thể chạy nhiều coroutines, một coroutine cũng có thể được chuyển qua lại giữa các thread, có thể suspend ở một thread và resume ở một thread khác.


-----Why
	Tất cả những "painful task" được thực hiện bằng RxJava, AsyncTask hoặc những methods khác như executors, HandlerThreads và IntentServices đều được thực hiện một cách đơn giản bằng coroutines
	Coroutines API cũng cho phép viết asynchronous code trong một sequential manner.
	Tránh những unneccessary boilerplate code đến từ các callbacks và làm cho code readable và maintainable.
-----Why we need asynchronous programming in android development?
	Most of the smartphones có refresh frequency thấp nhất là 60Hz. Điều này có nghĩa là ứng dụng sẽ refresh 60 lần mỗi giây (16.666ms cho mỗi lần refresh). Do đó, nếu ta chạy một ứng dụng mà nó sẽ vẽ trên màn hình bằng main thread mỗi 16.66s. ĐỒng thời, cũng có những smartphones trên thị trường có refresh frequency là 90Hz hoặc 120Hz, tượng tự, ứng dụng chỉ cần 11ms để thực hiện refresh trên main thread.
	Mặc định, android main thread sẽ có một bộ các regular responsibilities - nó sẽ luôn parse XML, inflate view components và draw chúng lặp lại mỗi lần REFRESH
	Main thread cũng phải lắng nghe các tương tác của người dùng như click events
	=> vì vậy, nếu ta viết quá nhiều task xử lý trên main thread, nếu thời gian thực thi của nó vượt quá thời gian siêu nhỏ giữa các lần refresh, ứng dụng sẽ cho thấy performance errors, freeze the screen, unpredictable behaviours. Như một kết quả của công nghệ, refresh frequency sẽ ngày càng ca, ta cần phải triển khai những tác vụ bất đồng bộ cần thời gian chạy dài trong một luồng riêng. Để đạt được điều đó, cách mới nhất, hiệu quả nhất chúng ta có hôm nay chính là Kotlin coroutines
	
