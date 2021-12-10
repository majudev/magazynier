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

Aktualnie wspierana jest jedynie instalacja przez Dockera. Aby zobaczyć
starą wersję tej instrukcji, stworzoną dla systemu Debian, otwórz plik
[INSTALL-Debian.md](INSTALL-Debian.md).

### Instalacja Dockera

### Budowanie kontenera
Klonujemy repozytorium ze skryptami dla Dockera, a następnie
budujemy obraz dockera.
```
git clone https://github.com/majudev/magazynier-docker.git
cd magazynier-docker
sudo docker build -t majudev/magazynier:0.1.1-ALPHA .
```
Po wpisaniu `sudo docker images` na liście powinien pojawić się nasz
kontener.

Do zamontowania kontenera potrzebny będzie katalog, w którym będziemy
trzymać konfigurację oraz dane systemu. Musimy też wybrać port, na którym
będzie dostępny interfejs.

Uruchamiamy kontener komendą:
```
sudo docker run \
    --name="magazynier" \          # nazwa kontenera
    -p 127.0.0.1:2233:80/tcp \     # system dostępny na localhost:2233
    -v /tmp/data:/data \           # dane systemu w /tmp/data
    majudev/magazynier:0.1.1-ALPHA # nazwa obrazu dockera
```
Zatrzymujemy kontener komendą `sudo docker stop magazynier`. Przechodzimy do
konfiguracji. Potem możemy włączyć kontener z powrotem używając
`sudo docker start magazynier`.

## Konfiguracja
System najlepiej skonfigurować poprzez plik `config.sh` obecny w folderze
podanym w komendzie `docker run` w poprzednim rozdziale.
Domyślna konfiguracja:
```
MYSQL_HOST="jdbc:mariadb://localhost:3306/magazynier"
MYSQL_USER="magazynier"
MYSQL_PASSWORD="magazynier"
MAIL_SERVER="localhost"
MAIL_PORT="25"
MAIL_USER=""
MAIL_PASSWORD=""
MAIL_TRANSPORT="SMTP"
SITE_BASEURL="http://localhost/magazynier"
SITE_APIURL="http://localhost/api"
```
- `MYSQL_HOST` to adres serwera MySQL w formacie dla Javy. Domyślnie localhost.
`/magazynier` to nazwa bazy danych.
- `MYSQL_USER` to nazwa użytkownika MySQL.
- `MYSQL_PASSWORD` to hasło użytkownika MySQL.
- `MAIL_SERVER` adres serwera SMTP. Domyślnie localhost, można użyć np. GMail.
- `MAIL_PORT` port serwera SMTP. Domyślnie `25`, czyli bez szyfrowania.
`587` dla STARTTLS, `465` dla SSL.
- `MAIL_USER`, `MAIL_PASSWORD` - nazwa użytkownika i hasło do serwera SMTP.
Używając GMaila trzeba najpierw zezwolić na niebezpieczne metody logowania
w ustawieniach konta.
- `MAIL_TRANSPORT` to rodzaj szyfrowania. `SMTP` oznacza brak szyfrowania,
`SMTP_TLS` dla STARTTLS (port 587), `SMTPS` dla SSL (465).
- `SITE_BASEURL` - to adres URL, pod którym znajdują się statyczne pliki systemu.
    Jeżeli nie zmieniałeś domyślnej konfiguracji, jest to
    `http://<IP KOMPUTERA>/magazynier`. Przy używaniu reverse proxy należy podać
    adres który będzie wpisywał użytkownik, np. `https://magazynier.example.org`.
- `SITE_APIURL` - analogicznie, jest to adres URL, pod którym dostępne są endpointy
  API. Aplikacja słucha na `http://localhost:8080/api`, ale nginx ma też domyślnie
  przekierowanie na `http://localhost/api`.

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