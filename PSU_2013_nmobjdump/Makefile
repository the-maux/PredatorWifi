

OBJD	=	my_objdump

SRC_NM	=	src/nm/main.c			\
		src/nm/list.c			\
		src/nm/type.c			\
		src/nm/sort.c			\

SRC_OBJD =	src/objdump/main.c		\
		src/objdump/head.c		\
		src/objdump/arg_s.c		\
		src/objdump/norme_epi.c		\
		src/objdump/print.c		\

SRC_SHARED=	src/shared/load.c		\
		src/shared/load_symbol.c	\

OBJ_NM	=	$(SRC_NM:.c=.o) $(SRC_SHARED:.c=.o)

OBJ_D=		$(SRC_OBJD:.c=.o) $(SRC_SHARED:.c=.o)

CFLAGS +=	-g  #-W -Wall -Wextra

all:		nm objdump

$(NM):		$(OBJ_NM)
		cc -o $(NM) $(OBJ_NM) $(CFLAGS)

$(OBJD):	$(OBJ_D)
		cc -o $(OBJD) $(OBJ_D) $(CFLAGS)

objdump:	$(OBJD)

nm:		$(NM)



clean_nm:
		rm -rvf $(OBJ_NM)
		rm -rvf $(OBJ_SHARED)

clean_obj:
		rm -rvf $(OBJ_D)
		rm -rvf $(OBJ_SHARED)
clean:
		rm -rvf $(OBJ_NM)
		rm -rvf $(OBJ_D)
		rm -rvf $(OBJ_SHARED)

fclean_nm:	clean_nm
		rm -rf $(NM)

fclean_obj:	clean_obj
		rm -rf $(OBJD)

fclean:		clean
		rm -rf $(NM)
		rm -rf $(OBJD)

re_nm:		fclean_nm $(NM)

re_objd:	fclean_nm $(OBJD)

re:		fclean $(NM) $(OBJD)
