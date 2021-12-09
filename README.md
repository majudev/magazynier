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
rzeczywistość niszczy nasze założenia - połowa namiotów nie ma oznaczeń, każdy
garnek ma inną wielkość, 1/3 materaców nadaje się tylko do izolatki... Brzmi
znajomo, prawda? Z tego powodu w Grupach przedmiotów można dowolnie je mieszać.
5 namiotów z oznaczeniami, 3 bez? Nie ma problemu. Komentarz przy każdym
garnku? Spoko. Notatka w którym miejscu na palecie leżą sztućce? Już się robi.

### Ograniczenia
Magazynier nie umie sobie na razie poradzić z ideą trzymania jednostek w innych
jednostkach. Przykładem takiego działania jest włożenie siekier do skrzyni,
a skrzyni na paletę. Magazynier może widzieć taką sytuację jako:
- Jednostkę "Skrzynia" zawierającą siekierę
- Jednostkę "Paleta" zawierającą skrzynię
- Jednostkę "Paleta" zawierającą siekierę

Ale **nie może** widzieć jej jako
- Jednostka "Paleta" zawierająca jednostkę "Skrzynia"
  - Jednostka "Skrzynia" zawierająca przedmiot "siekiera"

Ponieważ system Magazynier nie obsługuje wkładania jednostek na inne jednostki.
Rozwiązaniem tego problemu będzie planowany system **tagów**, który pozwoli
przypisywać przedmioty do definiowalnych grup, np _obóz_, _34 KDW_, ale też
_skrzynia 4_ czy _uszkodzone_.

## Instalacja
### Dla noobów
Patrz opcja "SaaS"

### Debian
Na razie brak.

### Docker
Na razie brak.

### SaaS
Coming soon.

## Demo
Wersję demo aplikacji można zobaczyć pod adresem _TODO_.

Kod źródłowy wersji demo znajduje się _TODO_.