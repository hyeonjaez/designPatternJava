# Singleton Pattern

##### 단 하나의 유일한 객체를 만들기 위한 코드 패턴

- 특정 클래스가 오직 하나의 인스턴스만을 갖도록 보장하고, 그 인스턴스에 대한 글로벌한 접근을 제공
- 리소스 공유, 설정 관리, 캐싱, 로깅등 여러 상황에서 쓰임

## 왜 써야하나?
- 객체를 생성할 때마다 메모리 영역을 할당 받아야한다. 하지만 한번의 new 를 통해 객체를 생성한다면 메모리 낭비를 방지할 수 있다.
- 싱글톤으로 구현한 인스턴스는 '전역'이므로, 다른 클래스의 인스턴스들이 데이터를 공유하는 것이 가능한 장점이 있음.

## 사용하는 경우는 언제인가?
- 주로 공통된 객체를 여러개 생성해서 사용해야하는 상황
- 데이터베이스에서 커넥션풀, 스레드풀, 캐시, 로그 기록 객체 등
- 안드로이드 앱 : 각 액티비티 들이나, 클래스마다 주요 클래스들을 하나하나 전달하는게 번거롭기 때문에 싱글톤 클래스를 만들어 어디서든 접근하도록 설계
- 인스턴스가 절대적으로 한 개만 존재하는 것을 보증하고 싶을 때 사용함
-----
```java
public class Singleton {
    private static Singleton instance;

    // private 생성자
    private Singleton() {
    }

    // 인스턴스 얻기
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

1. private 생성자 : 클래스 내부에서만 인스턴스를 생성할 수 있도록 생성자를 private으로 만든다.
2. private static instance : 클래스 내부에 하나의 private static 인스턴스를 만든다
3. getInstance() : 정적 메서드를 통해 클래스의 인스턴스를 얻을 수 있는 메서드를 제공, 이 메서드는 클래스의 인스턴스를 생성하고 , 이미 생성된 경우에는 기존 인스턴스를 반환
4. 지연 초기화 : 인스턴스를 처음부터 생성하는 것이 아닐, 인스턴스가 처음 요청될때 생성하도록 하는 방법을 사용함, 리소스를 절약할 수 있음
5. 동기화 : 멀티스레드 환경에서 동시에 getInstance() 메서드를 호출하면 여러개의 인스턴스가 생성될 수 있으므로, 동기화 메커니즘을 사용하여 스레드 안정성을 보장.
6. 직렬화 : 싱글톤 클래스가 직렬화되는 경우, 역직렬화 시에도 동일한 인스턴스를 유지하기 위한 작업을 추가해야 할 수 있다.

-----------------------

# 종류

### 1. Eager Initialization

- static fianl로 선언 해줘서 멀티 쓰레드 환경에서도 안전함
- 하지만 static 멤버는 당장 객체를 사용하지 않더라도 메모리에 적재하기 때문에 만약 리소스가 큰 객체 일 경우, 공간 자원 낭비가 발생함
- 예외 처리를 할 수 없음

```java
class Singleton {
    // 싱글톤 클래스 객체를 담을 인스턴스 변수
    private static final Singleton INSTANCE = new Singleton();

    // 생성자를 private로 선언 (외부에서 new 사용 X)
    private Singleton() {
    }

    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```

### 2. Static block initialization

- static block 을 이용하여 예외를 잡을 수 있다
- 하지만 static의 특성으로 공간을 차지한다.
-

```java
class Singleton {
    // 싱글톤 클래스 객체를 담을 인스턴스 변수
    private static Singleton instance;


    // 생성자를 private로 선언 (외부에서 new 사용 X)
    private Singleton() {
    }

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

### 3. Lazy initialization

- 객체 생성에 대한 관리를 내부적으로 처리
- 메서드를 호출했을 때 인스턴스 변수의 null 유무에 따라 초기화 하거나 있는 걸 반환하는 기법
- 위의 고정으로 메모리 차지의 한계점을 극복
- 하지만 Thread Safe 하지 않는 치명적인 단점을 가지고 있음
-

```java
class Singleton {
    // 싱글톤 클래스 객체를 담을 인스턴스 변수
    private static Singleton instance;

    // 생성자를 private로 선언 (외부에서 new 사용 X)
    private Singleton() {
    }

    // 외부에서 정적 메서드를 호출하면 그제서야 초기화 진행 (lazy)
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton(); // 오직 1개의 객체만 생성
        }
        return instance;
    }
}
```

-> 치명적인 단점
멀티 스레드 환경에서 객체가 두개 이상이 생성이 될수도 있다.

1. 스레드 A, 스레드 B가 존재한다고 하면
2. 스레드 A가 if문을 평가하고 인스턴스 생성 코드로 진입하였다.
3. 그때 B가 들어와서 if문을 평가하게 되면 아직 인스턴스 생성이 안되어서 B의 if문도 참이게 된다.
4. 결과적으로 A와 B가 인스턴스 초기화 코드를 두번 실행하여 객체가 두개가 만들어진다. (원자성 결여)

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Singleton[] singleton = new Singleton[100];

        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 0; i < 100; i++) {
            final int num = i;
            service.submit(() -> {
                singleton[num] = Singleton.getInstance();
            });
        }
        service.shutdown();

        for (Singleton s : singleton) {
            System.out.println(s.toString());
        }
    }
}
```

위 코드처럼 해시 코드를 찍어보면
chapter2.Singleton@6fadae5d
chapter2.Singleton@17f6480
chapter2.Singleton@17f6480
chapter2.Singleton@17f6480
chapter2.Singleton@17f6480
chapter2.Singleton@17f6480
chapter2.Singleton@17f6480
chapter2.Singleton@17f6480

이런식으로 싱글톤 클래스인데 객체가 두개 이상이 만들어져 버린다.

### 4. Thread safe initialization

- synchronized 키워드를 통해 메서드에 쓰레드들을 하나하나씩 접근하게 하도록 설정한다. (동기화)
- 하지만 여러개의 모듈들이 매번 객체를 가져올때마다 synchronized 메서드를 매번 호출하여 동기화 처리 작업에 overhead가 발생하여 성능 하락이 발생한다.

*info -> synchronized 키워드는 멀티 쓰레드 환경에서 두개 이상의 쓰레드가 하나의 변수에 동시에 접근할때 Race condition(경쟁상태)이 발생하지 않도록한다. 즉) 쓰레드가 방문할때 다른
쓰레드가 방문을 하지 못하도록 잠금을 거는것으로 보면 됨.

``` java
class Singleton {
    private static Singleton instance; 

    private Singleton() {}

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton(); 
                
            }
        return instance; 
    }
}
```

### 5. Double-Checked Locking

- 매번 synchronized 동기화를 실행하는 것이 문제라면, 최초 초기화할때만 적용하고 이미 만들어진 인스턴스를 반환할때는 사용하지 않도록 하는 기법
- 이때 인스턴스 필드에 volatile 키워드를 붙여주어야 I/O 불일치 문제를 해결 할 수 있다. ? I/O가 뭐임? 왜 불일치가 나지? -> I/O는 입출력이다. 불일치는 여러 스레드가 공유 자원에 접근하고
  변경할때 발생하할 수 있는 문제 이문제는 여러 스레드가 동시에 데이터를 읽거나 쓸 때 예상하지 못한 결과가 발생하는 상황을 가리킴.

- 하지만 이 키워드를 이용하려면 JVM 1.5이상이어야 되고, JVM에 대한 이해가 필요하고 JVM에 따라 쓰레드 세이프 하지 않는 경우가 발생하여 사용하기를 지양함

*volatile키워드

- 자바에서는 쓰레드를 여러개 사용할 경우, 성능을 위해 각각의 쓰레드들은 변수를 캐시 메모리에서 가져오게 됨
- 문제는 비동기로 변수값을 캐시에 저장하다가, 각 쓰레드마다 할당되어있는 캐시 메모리의 변수값이 일치 하지 않을 수 있다는 점이다.
- 그러하여 volatile 키워드를 통해 이 변수는 캐시에서 읽지 말고 메인 메모리에서 읽어오도록 지정해준다.

```java
class Singleton {
    private static volatile Singleton instance; // volatile 키워드 적용

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            // 메서드에 동기화 거는게 아닌, Singleton 클래스 자체를 동기화 걸어버림
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton(); // 최초 초기화만 동기화 작업이 일어나서 리소스 낭비를 최소화
                }
            }
        }
        return instance; // 최초 초기화가 되면 앞으로 생성된 인스턴스만 반환
    }
}
```

### 6. Bill Pugh Solution (LazyHolder)

- 권장하는 방법
- 멀티쓰레드 환경에서 안전하고 Lazy Loading(나중에 객체 생성) 도 가능한 완벽한 싱글톤 기법
- 클래스 안에 내부 클래스를 두어서 JVM의 클래스 로더 메커니즘과 클래스가 로드되는 시점을 이용한 방법(스레드 세이프함)
- static 메소드에서는 static 멤버만을 호출할 수 있기 때문에 내부 클래스를 static으로 설정
- 내부 클래스의 치명적인 문제점인 메모리 누수 문제를 해결하기 위해 내부 클래스를 static으로 설정
- 단, 클라이언트가 임의로 싱글톤을 파괴할 수 있다는 단점을 지님 (Reflection API, 직렬화/역직렬화를 통하여)

```java
class Singleton {

    private Singleton() {
    }

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

1) 내부 클래스를 static으로 선언해줌으로써 싱글톤 클래스가 초기화 되어도 SingleInstanceHolder내부 클래스는 메모리에 로드되지 않음
2) getInstance() 메서드를 호출 할때, SingleInstanceHolder 내부 클래스의 static멤버를 가져와서 리턴하게 되는데, 이때 내부 클래스가 한번만 초기화 되면서 싱글톤 객체를 최초로
   생성및 리턴하게 된다.
3) final로 지정함으로써 다시 값이 할당되지 않도록 방지

### 7. Enum 이용

- 권장하는 방법
- enum은 애초에 멤버를 만들때 private로 만들고 한번만 초기화 하기 때문에 thread safe한다
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

### 8. 정리를 하자면

- 싱글톤 패턴 클래스를 만들기 위해서는 Bill Pugh Solution 기법을 사용하거나 Enum으로 만들어서 사용하면 됨
- 다만 이 둘의 사용 선택은 싱글톤 클래스의 목적에 따라 갈리게 됨
-
    1. LaszHolder : 성능이 중요시 되는 환경
-
    2. Enum : 직렬화, 안정성 중요시 되는 환경

------

### 싱글톤 패턴은 안티 패턴?

- 고정된 메모리 영역을 가지고 하나의 인스턴스만 사용하기 때문에 메모리 낭비 방지 할 수 있으며, DataBase Connection Pool (DBCP) 처럼 공통된 객체를 여러개 생성해야 하는 상황에서 많이
  사용됨.
- 하지만 싱글톤 패턴은 얻는 이점과 더불어 많은 문제점들을 수반하기 때문에 trade-off 를 잘 고려해야함

trade-off? 이거 뭐임?
-> Trade-off란 어떤 결정을 할 때 한 가지 이점을 얻기 위해 다른 이점이나 희생해야 할 것들을 의미

----

### 싱글톤의 문제점

##### 1. 모듈간 의존성이 높아짐

- 대부분의 싱글톤을 이용하는 경우 인터페이스가 아닌 클래스의 객체를 미리 생성하고 정적 메소드를 이용해 사용하기 때문에 클래스 사이에 강한 의존성과 높은 결합이 생기게 된다.

##### 2. SOLID 원칙에 위배되는 사례가 많음

- 우선 싱글톤 인스턴스 자체가 하나만 생성하기 때문에 여러가지 책임을 지니게 되는 경우가 많아 단일 책임 원칙(SRP)를 위반하기도 하고, 싱글톤 인스턴스가 혼자 너무 많은 일을 하거나, 많은 데이터를 공유시키면
  다른 클래스들 간의 결합도가 높아지게 되어 개방-폐쇄 원칙(OCP)에도 위배된다.
- 의존 관계상 클라이언트가 인터페이스와 같은 추상화가 아닌, 구체 클래스에 의존하게 되어 의존 역전 원칙(DIP)도 위반하게 된다.

##### 3. TDD 단위 테스트에 애로사항이 있음

- 단위 테스트를 할때, 단위 테스트는 테스트가 서로 독립적이어야 하며 테스트를 어떤 순서로든 실행 할 수 있어야 하는데, 싱글톤 인스턴스는 자원을 공유하고 있기 때문에, 테스트가 결함없이 수행되려면 매번 인스턴스의
  상태를 초기화시켜주어야 한다. 그렇지 않으면 어플리케이션 전역에서 상태를 공유하기 때문에 테스트가 온전하게 수행되지 못할 수도 있다.

------

