# Magazynier
*Proste zarządzanie magazynem*

### Spis treści
* [Czym jest Magazynier?](#czym-jest-magazynier)
  * [Idea](#idea)
  * [Budowa programu](#budowa-programu)
  * [Ograniczenia](#ograniczenia)
* [Instalacja](#instalacja)
  * [Dla noobów](#dla-noobw)
  * [Debian](#debian)
  * [Docker](#docker)
  * [SaaS](#saas)
* [Konfiguracja](#konfiguracja)
* [Demo](#demo)

## Czym jest Magazynier?
### Idea
Magazynier został zaprojektowany z myślą o utrzymywaniu stanu magazynu średniej
wielkości jednostek organizacyjnych. Jednostki takie są na tyle duże, że Excel
przestaje być wystarczającym rozwiązaniem, ale na tyle małe, że nie opłaca się
implementować w nich profesjonalnych rozwiązań, ponieważ wymagają one zbyt dużo
nauki - posiadają zbyt dużo funkcji, ich działanie jest skomplikowane.

### Budowa programu
W Magazynierze istnieją następujące obiekty:
- Magazyny - reprezentują one fizyczne pomieszczenia składowania sprzętu
- Jednostki magazynowe - np. palety, pudełka, półki na regale
- Przedmioty - np. garnki, namioty, materace

Przedmioty dodatkowo są grupowane w **Grupy przedmiotów**. Dzieje się tak, ponieważ
niektóre przedmioty powinny mieć indywidualne numery (np. namioty), a inne są
z natury tylko liczone (np. garnki). Realia nauczyły nas, że często
rzeczywistość niszczy założenia - połowa namiotów nie ma oznaczeń, każdy
garnek ma inną wielkość, 1/3 materaców nadaje się tylko do izolatki... Brzmi
znajomo, prawda? Z tego powodu w Grupach przedmiotów można dowolnie je mieszać.
5 namiotów z oznaczeniami, 3 bez? Nie ma problemu. Komentarz przy każdym
garnku? Spoko. Notatka w którym miejscu na palecie leżą sztućce? Już się robi.

### Ograniczenia
Magazynier nie umie sobie na razie poradzić z ideą trzymania jednostek w innych
jednostkach. Przykładem takiego działania jest włożenie siekier do skrzyni,
a skrzyni na paletę. Magazynier może widzieć to jako jedną z 3 sytuacji:
- Jednostkę "Skrzynia" zawierającą siekierę
- Jednostkę "Paleta" zawierającą skrzynię
- Jednostkę "Paleta" zawierającą siekierę

Ale **nie może** widzieć jej jako
- Jednostka "Paleta" zawierająca jednostkę "Skrzynia"
  - Jednostka "Skrzynia" zawierająca przedmiot "siekiera"

Rozwiązaniem tego problemu będzie planowany system **tagów**, który pozwoli
przypisywać przedmioty do definiowalnych grup, np _obóz_, _34 KDW_, ale też
_skrzynia 4_ czy _uszkodzone_.

## Instalacja
### Dla noobów
Nie jesteś _"techniczny"_? Patrz opcja "SaaS".

### Debian
_Ta część poradnika może być nieaktualna._

Zależności:
- MariaDB 10.3+
- OpenJDK 11+
- Nginx (lub inny serwer WWW serwujący statyczne strony)
- Redis
- Serwer SMTP (lokalny lub zdalny, może być np. GMail)

Najlepiej instalować na dedykowanej VM-ce lub w kontenerze. Czysty system
w każdym razie.

Instalacja zależności:
```
sudo apt install mariadb-server-10.5 mariadb-client-10.5 openjdk-11-jre nginx-light redis-server wget exim4 unzip
```

Teraz trzeba pobrać odpowiednią paczkę ZIP ze
[strony wydań](https://github.com/majudev/magazynier/releases)

Zabezpieczamy serwer MySQL komendą `sudo mysql_secure_installation`. Tworzymy
bazę danych, użytkownika i hasło:
```
CREATE DATABASE magazynier;
CREATE USER 'magazynier'@'localhost' IDENTIFIED BY 'magazynier';
GRANT ALL PRIVILEGES ON magazynier.* TO 'magazynier'@'localhost';
FLUSH PRIVILEGES;
```
Oczywiście można wybrać inne nazwy/hasła, ale trzeba je potem zmienić w
konfiguracji aplikacji.

Otwieramy wypakowany wcześniej plik `schema.sql`. Nie możemy go zaimportować
do bazy od razu, ponieważ nie zawiera średników na końcu linii. Dodajemy je
ręcznie lub uruchamiamy
`cat schema.sql | sed 's/InnoDB/InnoDB;/g' | sed 's/increment by 1/increment by 1;/g' | sed -E 's/(unique \(.+\))/\1;/g' | sed -E 's/(references .+ \(.+\))/\1;/g' > schema.mariadb`.
Importujemy do bazy danych przez `sudo mysql -D magazynier < schema.mariadb`
(lub `sudo mysql -D magazynier < schema.sql` jeśli dodaliśmy ręcznie).

Konfigurujemy klienta pocztowego. Jeśli chcemy użyć lokalnego exim4, to
uruchamiamy `sudo dpkg-reconfigure exim4-config` i wybieramy `internet`
jako opcję dostarczania maili. Restartujemy exim4 komendą
`sudo service exim4 restart`.

Konfigurujemy Nginxa. Usuwamy zawartość `/etc/nginx/sites-available/default`
i wklejamy tam:
```
server {
        listen 80 default_server;
        listen [::]:80 default_server;

        root /var/www/html;

        index index.html;

        server_name _;

        location / {
                # First attempt to serve request as file, then
                # as directory, then fall back to displaying a 404.
                try_files \$uri \$uri/ =404;
        }

        location /api {
                proxy_pass http://127.0.0.1:8080;
        }
}
```
I kopiujemy do `/var/www/html` folder `magazynier`, zawierający statyczne pliki
strony. Restartujemy Nginxa `sudo service nginx restart`.

Tworzymy plik startowy:
```
#!/bin/bash
EXECUTABLE=Magazynier.jar
HOST="jdbc:mariadb://localhost:3306/magazynier"
USER="magazynier"
PASSWORD="magazynier"
MAIL_SERVER="localhost"
MAIL_PORT="25"
MAIL_USER=""
MAIL_PASSWORD=""
MAIL_TRANSPORT="SMTP"
MAIL_BASEURL="http://localhost/magazynier"
java -jar $EXECUTABLE \
        --spring.datasource.url=$HOST \
        --spring.datasource.username=$USER \
        --spring.datasource.password=$PASSWORD \
        --smtp.host=$MAIL_SERVER \
        --smtp.port=$MAIL_PORT \
        --smtp.user=$MAIL_USER \
        --smtp.password=$MAIL_PASSWORD \
        --smtp.transport=$MAIL_TRANSPORT \
        --smtp.baseurl="$MAIL_BASEURL"
```
i konfigurujemy go analogicznie do tego z sekcji Konfiguracja.

Przechodziny do sekcji [Konfiguracja](#konfiguracja). Nasz adres strony to
`http://<IP-KOMPUTERA>/magazynier`, a adres API to `http://<IP-KOMPUTERA>/api`.

Po skonfigurowaniu uruchamiamy serwer skryptem który stworzyliśmy.

### Docker
Na razie brak.

### SaaS
Coming soon.

## Konfiguracja
### Komponenty systemu
System składa się z 2 komponentów: _frontendu_, czyli statycznych plików html,
oraz _backendu_, czyli aplikacji w Javie która obsługuje zapytania API
z frontendu.

### Frontend
Aby system działał poprawnie, konieczna jest konfiguracja zarówno _front-_,
jak i _back-endu_. Konfiguracja frontendu jest prostsza. Znajduje się ona
w pliku `js/config.js`. Posiada 3 linijki:
```
var baseUrl = "http://localhost/magazynier";
var apiUrl = "http://localhost/api";
var appName = "Magazynier";
```
- `baseUrl` - to adres URL, pod którym znajdują się statyczne pliki systemu.
Jeżeli nie zmieniałeś domyślnej konfiguracji, jest to
`http://<IP KOMPUTERA>/magazynier`. Przy używaniu reverse proxy należy podać
adres który będzie wpisywał użytkownik, np. `https://magazynier.example.org`.
- `apiUrl` - analogicznie, jest to adres URL, pod którym dostępne są endpointy
API. Aplikacja słucha na `http://localhost:8080/api`.
- `appName` - jest to nazwa wyświetlana w lewym górnym rogu interfejsu
użytkownika. Domyślnie "Magazynier". Możesz tu wpisać np. nazwę swojej
jednostki.

### Backend
Backend najlepiej skonfigurować poprzez plik `backend.sh` obecny w przypadku
używania Dockera lub plik startowy w przypadku instalacji na Debianie.
Domyślna konfiguracja:
```
HOST="jdbc:mariadb://localhost:3306/magazynier"
USER="magazynier"
PASSWORD="magazynier"
MAIL_SERVER="localhost"
MAIL_PORT="25"
MAIL_USER=""
MAIL_PASSWORD=""
MAIL_TRANSPORT="SMTP"
MAIL_BASEURL="http://localhost/magazynier"
```
- `HOST` to adres serwera MySQL w formacie dla Javy. Domyślnie localhost.
`/magazynier` to nazwa bazy danych.
- `USER` to nazwa użytkownika MySQL.
- `PASSWORD` to hasło użytkownika MySQL.
- `MAIL_SERVER` adres serwera SMTP. Domyślnie localhost, można użyć np. GMail.
- `MAIL_PORT` port serwera SMTP. Domyślnie `25`, czyli bez szyfrowania.
`587` dla STARTTLS, `465` dla SSL.
- `MAIL_USER`, `MAIL_PASSWORD` - nazwa użytkownika i hasło do serwera SMTP.
Używając GMaila trzeba najpierw zezwolić na niebezpieczne metody logowania
w ustawieniach konta.
- `MAIL_TRANSPORT` to rodzaj szyfrowania. `SMTP` oznacza brak szyfrowania,
`SMTP_TLS` dla STARTTLS (port 587), `SMTPS` dla SSL (465).
- `MAIL_BASEURL` to adres frontendu do umieszczenia np. w mailach aktywujących
konto. Należy tu wpisać to co w `baseUrl` we frontendzie.

## Demo
Wersję demo aplikacji można zobaczyć pod adresem _TODO_.

Wersja demo kasuje wszystkie zmiany wprowadzone przez użytkownika co godzinę.

* Login: `admin`, Hasło: `Administrat0r`
* Login: `uzytkownik`, Hasło: `Uzytkown1k`

## Licencja
```
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
                    Version 2, December 2004

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FUCK YOU WANT TO.
  
  
```