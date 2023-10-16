 # Singleton Pattern

#### **1. Eager Initialization**
- 한번만 미리 만들어두는, 가장 직관적이면서도 심플한 기법
- static final이라 멀티 쓰레드 환경에서도 안전함
- 그러나 static 멤버는 당장 객체를 사용하지 않더라도 메모리에 적재하기 때문에 만일 리소스가 큰 객체일 경우, 공간 자원 낭비가 발생
- 예외 처리를 할 수 없음
```java
class Singleton {
    // 싱글톤 클래스 객체를 담을 인스턴스 변수
    private static final Singleton INSTANCE = new Singleton();

    // 생성자를 private로 선언 (외부에서 new 사용 X)
    private Singleton() {}

    public static Singleton getInstance() {
        return INSTANCE;
    }
}
//만일 싱글톤을 적용한 객체가 그리 크지 않은 객체라면 이 기법으로 적용해도 무리는 없다
```

==> 예외를 잡을 수 있도록
### **2. Static block initialization**
- static block을 통해서 예외를 잡을 수 있다.
- 그러나 여전히 static의 특성으로 사용하지도 않는데 공간을 차지한다.
  (static block: 클래스가 로딩되고 클래스 변수가 준비된 후 자동으로 실행되는 블록)
```java
class Singleton {
    // 싱글톤 클래스 객체를 담을 인스턴스 변수
    private static Singleton instance;

    // 생성자를 private로 선언 (외부에서 new 사용 X)
    private Singleton() {}
    
    // static 블록을 이용해 예외 처리
    static {
        try {
            instance = new Singleton();
        } catch (Exception e) {
            throw new RuntimeException("싱글톤 객체 생성 오류");
        }
    }

    public static Singleton getInstance() {
        return instance;
    }
}
```

==> 고정 메모리 차지의 한계점을 극복
### **3.  Lazy initialization**
- 객체 생성에 대한 관리를 내부적으로 처리
- 메소드를 호출했을 때 인스턴스 변수의 null 유무에 따라 초기화 하거나 있는 걸 반환하는 기법
- 이전의 미사용 고정 메모리 차지의 한계점을 극복
- 그러나 쓰레드 세이프하지 않는 치명적인 단점을 가지고 있음
```java
class Singleton {
    // 싱글톤 클래스 객체를 담을 인스턴스 변수
    private static Singleton instance;

    // 생성자를 private로 선언 (외부에서 new 사용 X)
    private Singleton() {}
	
    // 외부에서 정적 메서드를 호출하면 그제서야 초기화 진행 (lazy)
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton(); // 오직 1개의 객체만 생성
        }
        return instance;
    }
}
```
이 코드는 멀티 쓰레드 환경에서 치명적인 문제점을 가지고 있다. 다음과 같은 동시성으로 인한 코드 실행의 문제가 발생한다.
1) 스레드 A, B가 있다고 가정한다.
2) 스레드 A가 if문을 평가하고 인스턴스 생성 코드로 진입하였다. (아직 초기화 진행 X)
2. 그런데 그때 스레드 B가 if문을 평가한다. 아직 스레드 A가 인스턴스화 코드를 실행을 안시켰기 때문에 이 if문도 참이 되게 된다.
3. 그러면 결과적으로 스레드 A와 B가 인스턴스 초기화 코드를 두번 실행하는 꼴이 된다. (원자성이 결여)
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 싱글톤 객체
class Singleton {
    private static Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton(); // 오직 1개의 객체만 생성
        }
        return instance;
    }
}

public class Main {
    public static void main(String[] args) {
        // 1. 싱글톤 객체를 담을 배열
        Singleton[] singleton = new Singleton[10];

        // 2. 스레드 풀 생성
        ExecutorService service = Executors.newCachedThreadPool();

        // 3. 반복문을 통해 10개의 스레드가 동시에 인스턴스 생성
        for (int i = 0; i < 10; i++) {
            final int num = i;
            service.submit(() -> {
                singleton[num] = Singleton.getInstance();
            });
        }

        // 4. 종료
        service.shutdown();
		
        // 5. 싱글톤 객체 주소 출력
        for(Singleton s : singleton) {
            System.out.println(s.toString());
        }
    }
}
```

![design-Singleton-pattern](https://blog.kakaocdn.net/dn/dROjec/btrPvxa6mBj/b0DGKMAbyYGEA0raRxqt20/img.png)
이처럼 싱글톤 클래스인데 객체 두개가 만들어지게 된다.

==> 스레드 safe하게 하기 위해서
### **4. Thread safe initialization**
- synchronized 키워드를 통해 메서드에 쓰레드들을 하나하나씩 접근하게 하도록 설정한다. (동기화)
- 하지만 여러개의 모듈들이 매번 객체를 가져올 때 synchronized 메서드를 매번 호출하여 동기화 처리 작업에 overhead가 발생해 성능 하락이 발생한다.
```java
class Singleton {
    private static Singleton instance;

    private Singleton() {}

    // synchronized 메서드
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

### **5. Double-Checked Locking**
- 매번 synchronized 동기화를 실행하는 것이 문제라면, 최초 초기화할때만 적용하고 이미 만들어진 인스턴스를 반환할때는 사용하지 않도록 하는 기법
- 이때 인스턴스 필드에 volatile 키워드를 붙여주어야 I/O 불일치 문제를 해결 할 수 있다.
- 그러나 volatile 키워드를 이용하기위해선 JVM 1.5이상이어야 되고, JVM에 대한 심층적인 이해가 필요하여, JVM에 따라서 여전히 쓰레드 세이프 하지 않는 경우가 발생하기 때문에 사용하기를 지양하는 편이다.
  (캐시메모리: cpu와 주기억장치의 속도차이를 보완하기 위해 그 사이에 설치하는 반도체 기억장치)
- volatile키워드
  Java에서는 쓰레드를 여러개 사용할경우, 성능을 위해서 각각의 쓰레드들은 변수를 메인 메모리(RAM)으로부터 가져오는 것이 아니라 캐시(Cache) 메모리에서 가져오게 된다.  
  문제는 비동기로 변수값을 캐시에 저장하다가, 각 쓰레드마다 할당되어있는 캐시 메모리의 변수값이 일치하지 않을수 있다는 점이다.  
  그래서 ~~volatile~~ 키워드를 통해 이 변수는 캐시에서 읽지 말고 메인 메모리에서 읽어오도록 지정해주는 것이다.
```java
class Singleton {
    private static volatile Singleton instance; // volatile 키워드 적용

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
        	// 메서드에 동기화 거는게 아닌, Singleton 클래스 자체를 동기화 걸어버림
            synchronized (Singleton.class) { 
                if(instance == null) { 
                    instance = new Singleton(); // 최초 초기화만 동기화 작업이 일어나서 리소스 낭비를 최소화
                }
            }
        }
        return instance; // 최초 초기화가 되면 앞으로 생성된 인스턴스만 반환
    }
}
```

### **6. Bill Pugh Solution (LazyHolder) - 권장!**
- 권장되는 두가지 방법중 하나
- 멀티쓰레드 환경에서 안전하고 Lazy Loading(나중에 객체 생성) 도 가능한 완벽한 싱글톤 기법
- 클래스 안에 내부 클래스(holder)를 두어 JVM의 클래스 로더 매커니즘과 클래스가 로드되는 시점을 이용한 방법 (스레드 세이프함)
- static 메소드에서는 static 멤버만을 호출할 수 있기 때문에 내부 클래스를 static으로 설정  
  이밖에도 내부 클래스의 치명적인 문제점인 메모리 누수 문제를 해결하기 위하여 내부 클래스를 static으로 설정
- 다만 클라이언트가 임의로 싱글톤을 파괴할 수 있다는 단점을 지님 (Reflection API, 직렬화/역직렬화를 통해)
  -> Bill Pugh Solution에서 "초기화 시점을 클래스 로더 매커니즘에 맡긴다"는 것은 LazyHolder 클래스를 내부 클래스로 선언하고, LazyHolder 클래스가 처음으로 사용되는 시점에 클래스 로더에 의해 초기화되도록 하는 것을 의미합니다. 이렇게 하면 다음과 같은 특징이 나타납니다:
- 클래스 로더는 LazyHolder 클래스를 초기화할 때 스레드 간에 동기화를 보장합니다.
- LazyHolder 클래스의 초기화는 클래스 로더에 의해 한 번만 수행되며, 스레드 안전하게 처리됩니다.
- 싱글톤 인스턴스는 Lazy Loading 방식으로 초기화되어 실제로 필요한 시점에 생성됩니다.
```java
class Singleton {

    private Singleton() {}

    // static 내부 클래스를 이용
    // Holder로 만들어, 클래스가 메모리에 로드되지 않고 getInstance 메서드가 호출되어야 로드됨
    private static class SingleInstanceHolder {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return SingleInstanceHolder.INSTANCE;
    }
}
```

### **7. Enum 이용 - 권장!**
- enum은 애초에 멤버를 만들때 private로 만들고 한번만 초기화 하기 때문에 thread safe함.
- enum 내에서 상수 뿐만 아니라, 변수나 메서드를 선언해 사용이 가능하기 때문에, 이를 이용해 싱글톤 클래스 처럼 응용이 가능
- 위의 Bill Pugh Solution 기법과 달리, 클라이언트에서 Reflection을 통한 공격에도 안전
- 하지만 만일 싱글톤 클래스를 멀티톤(일반적인 클래스)로 마이그레이션 해야할때 처음부터 코드를 다시 짜야 되는 단점이 존재한다. (개발 스펙은 언제어디서 변경 될수 있기 때문에)
- 클래스 상속이 필요할때, enum 외의 클래스 상속은 불가능하다.
```java
enum SingletonEnum {
    INSTANCE;

    private final Client dbClient;
	
    SingletonEnum() {
        dbClient = Database.getClient();
    }

    public static SingletonEnum getInstance() {
        return INSTANCE;
    }

    public Client getClient() {
        return dbClient;
    }
}

public class Main {
    public static void main(String[] args) {
        SingletonEnum singleton = SingletonEnum.getInstance();
        singleton.getClient();
    }
}
```
`enum` 상수는 JVM(Java Virtual Machine) 내부적으로 상수 풀(Constant Pool)에 저장됩니다. 이 상수 풀은 메모리의 특정 영역에 위치하며, `enum` 상수들은 해당 상수 풀에 저장되어 공유됩니다. `enum` 타입은 클래스처럼 정의되지만, `enum` 상수는 메모리에서는 한 번만 초기화되고 다시 초기화되지 않습니다. 따라서 `enum` 상수는 런타임 중에 수정되거나 다시 할당되지 않으며, 싱글톤처럼 동작합니다.

Java의 가비지 컬렉터(Garbage Collector)는 더 이상 참조되지 않는 객체를 자동으로 감지하고 정리합니다. 즉, 사용하지 않는 `enum` 상수는 다른 객체에 의해 참조되지 않는 한 가비지 컬렉터의 대상이 됩니다. 따라서 메모리 누수가 발생하지 않습니다.

- LaszHolder : 성능이 중요시 되는 환경
- Enum : 직렬화, 안정성 중요시 되는 환경
